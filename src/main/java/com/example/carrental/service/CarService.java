package com.example.carrental.service;

import com.example.carrental.dto.CarRequestDto;
import com.example.carrental.dto.CarResponseDto;
import com.example.carrental.entity.Car;
import com.example.carrental.entity.CarDetails;
import com.example.carrental.enums.FuelType;
import com.example.carrental.exception.BadRequestException;
import com.example.carrental.exception.ResourceNotFoundException;
import com.example.carrental.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    public List<CarResponseDto> getAllCars() {
        return carRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    public CarResponseDto getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));

        return mapToResponseDto(car);
    }

    public CarResponseDto createCar(CarRequestDto requestDto) {
        FuelType fuelType;

        try {
            fuelType = FuelType.valueOf(requestDto.getFuelType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid fuel type");
        }

        Car car = new Car();
        car.setBrand(requestDto.getBrand());
        car.setModel(requestDto.getModel());
        car.setYear(requestDto.getYear());
        car.setFuelType(fuelType);
        car.setPricePerDay(requestDto.getPricePerDay());

        CarDetails details = new CarDetails();
        details.setMileage(requestDto.getMileage());
        details.setColor(requestDto.getColor());
        details.setSeats(requestDto.getSeats());
        details.setTransmission(requestDto.getTransmission());

        details.setCar(car);
        car.setDetails(details);

        Car savedCar = carRepository.save(car);

        return mapToResponseDto(savedCar);
    }

    private CarResponseDto mapToResponseDto(Car car) {
        return new CarResponseDto(
                car.getId(),
                car.getBrand(),
                car.getModel(),
                car.getYear(),
                car.getFuelType().name(),
                car.getPricePerDay(),
                car.getDetails().getMileage(),
                car.getDetails().getColor(),
                car.getDetails().getSeats(),
                car.getDetails().getTransmission()
        );
    }
}