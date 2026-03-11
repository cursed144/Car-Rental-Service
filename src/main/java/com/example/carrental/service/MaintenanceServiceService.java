package com.example.carrental.service;

import com.example.carrental.dto.ServiceRequestDto;
import com.example.carrental.dto.ServiceResponseDto;
import com.example.carrental.entity.MaintenanceService;
import com.example.carrental.exception.BadRequestException;
import com.example.carrental.exception.ResourceNotFoundException;
import com.example.carrental.repository.MaintenanceServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaintenanceServiceService {

    private final MaintenanceServiceRepository maintenanceServiceRepository;

    public List<ServiceResponseDto> getAllServices() {
        return maintenanceServiceRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    public ServiceResponseDto getServiceById(Long id) {
        MaintenanceService service = maintenanceServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        return mapToResponseDto(service);
    }

    public ServiceResponseDto createService(ServiceRequestDto requestDto) {
        if (maintenanceServiceRepository.existsByName(requestDto.getName())) {
            throw new BadRequestException("Service name already exists");
        }

        MaintenanceService service = new MaintenanceService();
        service.setName(requestDto.getName());
        service.setDescription(requestDto.getDescription());

        MaintenanceService savedService = maintenanceServiceRepository.save(service);

        return mapToResponseDto(savedService);
    }

    public ServiceResponseDto updateService(Long id, ServiceRequestDto requestDto) {
        MaintenanceService service = maintenanceServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        if (maintenanceServiceRepository.existsByNameAndIdNot(requestDto.getName(), id)) {
            throw new BadRequestException("Service name already exists");
        }

        service.setName(requestDto.getName());
        service.setDescription(requestDto.getDescription());

        MaintenanceService updatedService = maintenanceServiceRepository.save(service);

        return mapToResponseDto(updatedService);
    }

    public void deleteService(Long id) {
        MaintenanceService service = maintenanceServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        maintenanceServiceRepository.delete(service);
    }

    private ServiceResponseDto mapToResponseDto(MaintenanceService service) {
        return new ServiceResponseDto(
                service.getId(),
                service.getName(),
                service.getDescription()
        );
    }
}