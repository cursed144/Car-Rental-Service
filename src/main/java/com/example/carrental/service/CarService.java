package com.example.carrental.service;

import com.example.carrental.dto.CarRequestDto;
import com.example.carrental.dto.CarResponseDto;
import com.example.carrental.entity.Car;
import com.example.carrental.entity.CarDetails;
import com.example.carrental.entity.MaintenanceService;
import com.example.carrental.enums.FuelType;
import com.example.carrental.exception.BadRequestException;
import com.example.carrental.exception.ResourceNotFoundException;
import com.example.carrental.mapper.CarMapper;
import com.example.carrental.repository.CarRepository;
import com.example.carrental.repository.MaintenanceServiceRepository;
import com.example.carrental.repository.RentalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CarService {

    private final CarRepository carRepository;
    private final RentalRepository rentalRepository;
    private final MaintenanceServiceRepository maintenanceServiceRepository;
    private final CarMapper carMapper;

    @Transactional
    public List<CarResponseDto> getAllCars() {
        return carRepository.findAll().stream().map(carMapper::toDto).toList();
    }

    @Transactional
    public CarResponseDto getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));
        return carMapper.toDto(car);
    }

    public CarResponseDto createCar(CarRequestDto requestDto) {
        Car car = new Car();
        applyRequest(car, requestDto);
        return carMapper.toDto(carRepository.save(car));
    }

    public CarResponseDto updateCar(Long id, CarRequestDto requestDto) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));
        applyRequest(car, requestDto);
        return carMapper.toDto(carRepository.save(car));
    }

    public void deleteCar(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));

        if (rentalRepository.existsByCarId(id)) {
            throw new BadRequestException("Cannot delete car because it has rentals");
        }

        if (car.getServices() != null && !car.getServices().isEmpty()) {
            car.getServices().clear();
        }

        carRepository.save(car);
        carRepository.delete(car);
    }

    public void addServiceToCar(Long carId, Long serviceId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));

        MaintenanceService service = maintenanceServiceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        boolean alreadyLinked = car.getServices().stream().anyMatch(s -> s.getId().equals(serviceId));
        if (alreadyLinked) {
            throw new BadRequestException("Service is already assigned to this car");
        }

        car.getServices().add(service);
        carRepository.save(car);
    }

    public void removeServiceFromCar(Long carId, Long serviceId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));

        boolean removed = car.getServices().removeIf(s -> s.getId().equals(serviceId));
        if (!removed) {
            throw new ResourceNotFoundException("Service is not assigned to this car");
        }

        carRepository.save(car);
    }

    private void applyRequest(Car car, CarRequestDto requestDto) {
        FuelType fuelType;
        try {
            fuelType = FuelType.valueOf(requestDto.getFuelType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid fuel type");
        }

        car.setBrand(requestDto.getBrand());
        car.setModel(requestDto.getModel());
        car.setYear(requestDto.getYear());
        car.setFuelType(fuelType);
        car.setPricePerDay(requestDto.getPricePerDay());

        CarDetails details = car.getDetails();
        if (details == null) {
            details = new CarDetails();
            details.setCar(car);
            car.setDetails(details);
        }

        details.setDescription(requestDto.getDescription());
        details.setMileage(requestDto.getMileage());
        details.setColor(requestDto.getColor());
        details.setSeats(requestDto.getSeats());
        details.setTransmission(requestDto.getTransmission());
    }
}