package com.example.carrental.mapper;

import com.example.carrental.dto.RentalResponseDto;
import com.example.carrental.entity.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RentalMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "carId", source = "car.id")
    @Mapping(target = "status", expression = "java(rental.getStatus().name())")
    RentalResponseDto toDto(Rental rental);
}
