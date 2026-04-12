package com.example.carrental.mapper;

import com.example.carrental.dto.ServiceResponseDto;
import com.example.carrental.entity.MaintenanceService;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ServiceMapper {
    ServiceResponseDto toDto(MaintenanceService service);
}
