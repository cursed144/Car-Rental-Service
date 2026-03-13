package com.example.carrental.controller;

import com.example.carrental.dto.RentalRequestDto;
import com.example.carrental.dto.RentalResponseDto;
import com.example.carrental.service.RentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public ResponseEntity<List<RentalResponseDto>> getAllRentals() {
        return ResponseEntity.ok(rentalService.getAllRentals());
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<RentalResponseDto> getRentalById(@PathVariable Long id) {
        return ResponseEntity.ok(rentalService.getRentalById(id));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping
    public ResponseEntity<RentalResponseDto> createRental(@Valid @RequestBody RentalRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rentalService.createRental(requestDto));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<RentalResponseDto> updateRental(
            @PathVariable Long id,
            @Valid @RequestBody RentalRequestDto requestDto) {
        return ResponseEntity.ok(rentalService.updateRental(id, requestDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRental(@PathVariable Long id) {
        rentalService.deleteRental(id);
        return ResponseEntity.noContent().build();
    }
}