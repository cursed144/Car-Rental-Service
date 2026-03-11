package com.example.carrental.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RentalRequestDto {

    private Long userId;

    private Long carId;

    private LocalDate startDate;

    private LocalDate endDate;
}