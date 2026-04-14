package com.example.carrental.mapper;

import com.example.carrental.dto.CarResponseDto;
import com.example.carrental.entity.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CarImageMapper.class)
public interface CarMapper {

    @Mapping(target = "fuelType", expression = "java(car.getFuelType().name())")
    @Mapping(target = "description", source = "details.description")
    @Mapping(target = "mileage", source = "details.mileage")
    @Mapping(target = "color", source = "details.color")
    @Mapping(target = "seats", source = "details.seats")
    @Mapping(target = "transmission", source = "details.transmission")
    @Mapping(
            target = "serviceIds",
            expression = "java(car.getServices().stream()" +
                    ".map(com.example.carrental.entity.MaintenanceService::getId)" +
                    ".sorted()" +
                    ".toList())"
    )
    CarResponseDto toDto(Car car);
}