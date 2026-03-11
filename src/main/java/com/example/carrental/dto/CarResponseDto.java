package com.example.carrental.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class CarResponseDto {

    private Long id;
    private String brand;
    private String model;
    private int year;
    private String fuelType;
    private BigDecimal pricePerDay;
    private int mileage;
    private String color;
    private int seats;
    private String transmission;
}