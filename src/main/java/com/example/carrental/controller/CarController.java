package com.example.carrental.controller;

import com.example.carrental.dto.CarRequestDto;
import com.example.carrental.dto.CarResponseDto;
import com.example.carrental.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @GetMapping
    public ResponseEntity<List<CarResponseDto>> getAllCars() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarResponseDto> getCarById(@PathVariable Long id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }

    @PostMapping
    public ResponseEntity<CarResponseDto> createCar(@Valid @RequestBody CarRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carService.createCar(requestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarResponseDto> updateCar(
            @PathVariable Long id,
            @Valid @RequestBody CarRequestDto requestDto) {
        return ResponseEntity.ok(carService.updateCar(id, requestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{carId}/services/{serviceId}")
    public ResponseEntity<Void> addServiceToCar(
            @PathVariable Long carId,
            @PathVariable Long serviceId) {

        carService.addServiceToCar(carId, serviceId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{carId}/services/{serviceId}")
    public ResponseEntity<Void> removeServiceFromCar(
            @PathVariable Long carId,
            @PathVariable Long serviceId) {

        carService.removeServiceFromCar(carId, serviceId);

        return ResponseEntity.noContent().build();
    }
}