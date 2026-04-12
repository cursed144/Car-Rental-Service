package com.example.carrental.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

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
    private String description;
    private int mileage;
    private String color;
    private int seats;
    private String transmission;
    private List<Long> serviceIds;
    private List<CarImageResponseDto> images;
}
