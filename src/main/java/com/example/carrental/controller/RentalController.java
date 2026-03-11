package com.example.carrental.controller;

import com.example.carrental.dto.RentalRequestDto;
import com.example.carrental.dto.RentalResponseDto;
import com.example.carrental.service.RentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @GetMapping
    public ResponseEntity<List<RentalResponseDto>> getAllRentals() {
        return ResponseEntity.ok(rentalService.getAllRentals());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalResponseDto> getRentalById(@PathVariable Long id) {
        return ResponseEntity.ok(rentalService.getRentalById(id));
    }

    @PostMapping
    public ResponseEntity<RentalResponseDto> createRental(@Valid @RequestBody RentalRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rentalService.createRental(requestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RentalResponseDto> updateRental(
            @PathVariable Long id,
            @Valid @RequestBody RentalRequestDto requestDto) {
        return ResponseEntity.ok(rentalService.updateRental(id, requestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRental(@PathVariable Long id) {
        rentalService.deleteRental(id);
        return ResponseEntity.noContent().build();
    }
}