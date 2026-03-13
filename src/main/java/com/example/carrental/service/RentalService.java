package com.example.carrental.service;

import com.example.carrental.dto.RentalRequestDto;
import com.example.carrental.dto.RentalResponseDto;
import com.example.carrental.entity.Car;
import com.example.carrental.entity.Rental;
import com.example.carrental.entity.User;
import com.example.carrental.exception.BadRequestException;
import com.example.carrental.exception.ResourceNotFoundException;
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
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;

    public List<RentalResponseDto> getAllRentals() {
        return rentalRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    public RentalResponseDto getRentalById(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found"));

        enforceRentalAccess(rental);

        return mapToResponseDto(rental);
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

        if (requestDto.getStartDate() == null || requestDto.getEndDate() == null) {
            throw new BadRequestException("Start date and end date are required");
        }

        if (requestDto.getEndDate().isBefore(requestDto.getStartDate())) {
            throw new BadRequestException("End date cannot be before start date");
        }

        boolean carUnavailable = rentalRepository
                .existsByCarIdAndStartDateLessThanAndEndDateGreaterThan(
                        requestDto.getCarId(),
                        requestDto.getEndDate(),
                        requestDto.getStartDate()
                );

        if (carUnavailable) {
            throw new BadRequestException("Car is already rented for the selected period");
        }

        long days = ChronoUnit.DAYS.between(requestDto.getStartDate(), requestDto.getEndDate());
        if (days == 0) {
            days = 1;
        }

        BigDecimal totalPrice = car.getPricePerDay().multiply(BigDecimal.valueOf(days));

        Rental rental = new Rental();
        rental.setUser(user);
        rental.setCar(car);
        rental.setStartDate(requestDto.getStartDate());
        rental.setEndDate(requestDto.getEndDate());
        rental.setTotalPrice(totalPrice);

        Rental savedRental = rentalRepository.save(rental);

        return mapToResponseDto(savedRental);
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

        if (requestDto.getStartDate() == null || requestDto.getEndDate() == null) {
            throw new BadRequestException("Start date and end date are required");
        }

        if (requestDto.getEndDate().isBefore(requestDto.getStartDate())) {
            throw new BadRequestException("End date cannot be before start date");
        }

        boolean carUnavailable = rentalRepository
                .existsByCarIdAndStartDateLessThanAndEndDateGreaterThanAndIdNot(
                        requestDto.getCarId(),
                        requestDto.getEndDate(),
                        requestDto.getStartDate(),
                        id
                );

        if (carUnavailable) {
            throw new BadRequestException("Car is already rented for the selected period");
        }

        long days = ChronoUnit.DAYS.between(requestDto.getStartDate(), requestDto.getEndDate());
        if (days == 0) {
            days = 1;
        }

        BigDecimal totalPrice = car.getPricePerDay().multiply(BigDecimal.valueOf(days));

        rental.setUser(user);
        rental.setCar(car);
        rental.setStartDate(requestDto.getStartDate());
        rental.setEndDate(requestDto.getEndDate());
        rental.setTotalPrice(totalPrice);

        Rental updatedRental = rentalRepository.save(rental);

        return mapToResponseDto(updatedRental);
    }

    public void deleteRental(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found"));

        rentalRepository.delete(rental);
    }

    private RentalResponseDto mapToResponseDto(Rental rental) {
        return new RentalResponseDto(
                rental.getId(),
                rental.getUser().getId(),
                rental.getCar().getId(),
                rental.getStartDate(),
                rental.getEndDate(),
                rental.getTotalPrice()
        );
    }

    private CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (CustomUserDetails) authentication.getPrincipal();
    }

    private boolean isAdmin(CustomUserDetails user) {
        return user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
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