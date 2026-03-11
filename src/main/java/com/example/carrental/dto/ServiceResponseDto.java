package com.example.carrental.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ServiceResponseDto {

    private Long id;
    private String name;
    private String description;
}