package com.example.carrental.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CarRequestDto {

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @Min(value = 1900, message = "Year must be at least 1900")
    private int year;

    @NotBlank(message = "Fuel type is required")
    private String fuelType;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price per day must be greater than 0")
    private BigDecimal pricePerDay;

    @Min(value = 0, message = "Mileage cannot be negative")
    private int mileage;

    @NotBlank(message = "Color is required")
    private String color;

    @Min(value = 1, message = "Seats must be at least 1")
    private int seats;

    @NotBlank(message = "Transmission is required")
    private String transmission;
}