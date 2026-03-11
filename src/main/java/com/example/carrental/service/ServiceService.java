package com.example.carrental.service;

import com.example.carrental.dto.ServiceRequestDto;
import com.example.carrental.dto.ServiceResponseDto;
import com.example.carrental.entity.Service;
import com.example.carrental.exception.BadRequestException;
import com.example.carrental.exception.ResourceNotFoundException;
import com.example.carrental.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public List<ServiceResponseDto> getAllServices() {
        return serviceRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    public ServiceResponseDto getServiceById(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        return mapToResponseDto(service);
    }

    public ServiceResponseDto createService(ServiceRequestDto requestDto) {
        if (serviceRepository.existsByName(requestDto.getName())) {
            throw new BadRequestException("Service name already exists");
        }

        Service service = new Service();
        service.setName(requestDto.getName());
        service.setDescription(requestDto.getDescription());

        Service savedService = serviceRepository.save(service);

        return mapToResponseDto(savedService);
    }

    public ServiceResponseDto updateService(Long id, ServiceRequestDto requestDto) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        if (serviceRepository.existsByNameAndIdNot(requestDto.getName(), id)) {
            throw new BadRequestException("Service name already exists");
        }

        service.setName(requestDto.getName());
        service.setDescription(requestDto.getDescription());

        Service updatedService = serviceRepository.save(service);

        return mapToResponseDto(updatedService);
    }

    public void deleteService(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        serviceRepository.delete(service);
    }

    private ServiceResponseDto mapToResponseDto(Service service) {
        return new ServiceResponseDto(
                service.getId(),
                service.getName(),
                service.getDescription()
        );
    }
}