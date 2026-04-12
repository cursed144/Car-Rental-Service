package com.example.carrental.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CarImageResponseDto {
    private Long id;
    private String fileName;
    private String fileType;
    private Long carId;
}
