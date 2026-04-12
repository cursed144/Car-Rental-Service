package com.example.carrental.controller;

import com.example.carrental.dto.CarImageRequestDto;
import com.example.carrental.dto.CarImageResponseDto;
import com.example.carrental.service.CarImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars/{carId}/images")
@RequiredArgsConstructor
public class CarImageController {

    private final CarImageService carImageService;

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public ResponseEntity<List<CarImageResponseDto>> getImages(@PathVariable Long carId) {
        return ResponseEntity.ok(carImageService.getCarImages(carId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CarImageResponseDto> addImage(@PathVariable Long carId, @Valid @RequestBody CarImageRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carImageService.addImage(carId, requestDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long carId, @PathVariable Long imageId) {
        carImageService.deleteImage(carId, imageId);
        return ResponseEntity.noContent().build();
    }
}
