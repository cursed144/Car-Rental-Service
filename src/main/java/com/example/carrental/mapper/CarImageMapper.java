package com.example.carrental.mapper;

import com.example.carrental.dto.CarImageResponseDto;
import com.example.carrental.entity.CarImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CarImageMapper {

    @Mapping(target = "carId", source = "car.id")
    CarImageResponseDto toDto(CarImage image);
}
