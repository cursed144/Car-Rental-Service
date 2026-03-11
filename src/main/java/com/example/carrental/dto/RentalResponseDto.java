package com.example.carrental.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class RentalResponseDto {

    private Long id;

    private Long userId;

    private Long carId;

    private LocalDate startDate;

    private LocalDate endDate;

    private Double totalPrice;
}