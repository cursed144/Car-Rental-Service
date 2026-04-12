package com.example.carrental.service;

import com.example.carrental.dto.RentalRequestDto;
import com.example.carrental.dto.RentalResponseDto;
import com.example.carrental.entity.Car;
import com.example.carrental.entity.Rental;
import com.example.carrental.entity.User;
import com.example.carrental.enums.RentalStatus;
import com.example.carrental.exception.BadRequestException;
import com.example.carrental.exception.ResourceNotFoundException;
import com.example.carrental.mapper.RentalMapper;
import com.example.carrental.repository.CarRepository;
import com.example.carrental.repository.RentalRepository;
import com.example.carrental.repository.UserRepository;
import com.example.carrental.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final RentalMapper rentalMapper;

    public List<RentalResponseDto> getAllRentals() {
        CustomUserDetails currentUser = getCurrentUser();
        if (isAdmin(currentUser)) {
            return rentalRepository.findAll().stream().map(rentalMapper::toDto).toList();
        }
        return rentalRepository.findAllByUserId(currentUser.getId()).stream().map(rentalMapper::toDto).toList();
    }

    public RentalResponseDto getRentalById(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found"));
        enforceRentalAccess(rental);
        return rentalMapper.toDto(rental);
    }

    public RentalResponseDto createRental(RentalRequestDto requestDto) {
        CustomUserDetails currentUser = getCurrentUser();
        if (!isAdmin(currentUser) && !currentUser.getId().equals(requestDto.getUserId())) {
            throw new AccessDeniedException("You can create rentals only for your own account");
        }

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Car car = carRepository.findById(requestDto.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));

        validateDates(requestDto.getStartDate(), requestDto.getEndDate());
        ensureAvailability(requestDto.getCarId(), requestDto.getStartDate(), requestDto.getEndDate(), null);

        Rental rental = new Rental();
        rental.setUser(user);
        rental.setCar(car);
        rental.setStartDate(requestDto.getStartDate());
        rental.setEndDate(requestDto.getEndDate());
        rental.setTotalPrice(calculatePrice(car, requestDto.getStartDate(), requestDto.getEndDate()));
        rental.setStatus(resolveStatusForCreate(requestDto.getStatus(), requestDto.getStartDate(), requestDto.getEndDate()));

        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    public RentalResponseDto updateRental(Long id, RentalRequestDto requestDto) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found"));
        enforceRentalAccess(rental);

        CustomUserDetails currentUser = getCurrentUser();
        if (!isAdmin(currentUser) && !currentUser.getId().equals(requestDto.getUserId())) {
            throw new AccessDeniedException("You can update rentals only for your own account");
        }

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Car car = carRepository.findById(requestDto.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));

        validateDates(requestDto.getStartDate(), requestDto.getEndDate());
        ensureAvailability(requestDto.getCarId(), requestDto.getStartDate(), requestDto.getEndDate(), id);

        rental.setUser(user);
        rental.setCar(car);
        rental.setStartDate(requestDto.getStartDate());
        rental.setEndDate(requestDto.getEndDate());
        rental.setTotalPrice(calculatePrice(car, requestDto.getStartDate(), requestDto.getEndDate()));
        rental.setStatus(resolveStatusForUpdate(requestDto.getStatus(), requestDto.getStartDate(), requestDto.getEndDate()));

        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    public void deleteRental(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found"));
        rentalRepository.delete(rental);
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new BadRequestException("Start date and end date are required");
        }
        if (endDate.isBefore(startDate)) {
            throw new BadRequestException("End date cannot be before start date");
        }
    }

    private void ensureAvailability(Long carId, LocalDate startDate, LocalDate endDate, Long excludeRentalId) {
        boolean unavailable = excludeRentalId == null
                ? rentalRepository.existsByCarIdAndStartDateLessThanAndEndDateGreaterThan(carId, endDate, startDate)
                : rentalRepository.existsByCarIdAndStartDateLessThanAndEndDateGreaterThanAndIdNot(carId, endDate, startDate, excludeRentalId);
        if (unavailable) {
            throw new BadRequestException("Car is already rented for the selected period");
        }
    }

    private BigDecimal calculatePrice(Car car, LocalDate startDate, LocalDate endDate) {
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        if (days == 0) {
            days = 1;
        }
        return car.getPricePerDay().multiply(BigDecimal.valueOf(days));
    }

    private RentalStatus resolveStatusForCreate(String status, LocalDate startDate, LocalDate endDate) {
        if (status != null && !status.isBlank()) {
            return parseStatus(status);
        }
        LocalDate today = LocalDate.now();
        if (endDate.isBefore(today)) {
            return RentalStatus.COMPLETED;
        }
        if (startDate.isAfter(today)) {
            return RentalStatus.PENDING;
        }
        return RentalStatus.ACTIVE;
    }

    private RentalStatus resolveStatusForUpdate(String status, LocalDate startDate, LocalDate endDate) {
        return status != null && !status.isBlank()
                ? parseStatus(status)
                : resolveStatusForCreate(null, startDate, endDate);
    }

    private RentalStatus parseStatus(String status) {
        try {
            return RentalStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid rental status");
        }
    }

    private CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (CustomUserDetails) authentication.getPrincipal();
    }

    private boolean isAdmin(CustomUserDetails user) {
        return user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private void enforceRentalAccess(Rental rental) {
        CustomUserDetails currentUser = getCurrentUser();
        if (isAdmin(currentUser)) {
            return;
        }
        if (!rental.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You cannot access another user's rental");
        }
    }
}
