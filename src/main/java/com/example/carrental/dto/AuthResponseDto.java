package com.example.carrental.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponseDto {

    private String message;
    private Long userId;
    private String username;
    private String role;
}