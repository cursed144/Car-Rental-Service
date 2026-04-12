package com.example.carrental.service;

import com.example.carrental.dto.CarImageRequestDto;
import com.example.carrental.dto.CarImageResponseDto;
import com.example.carrental.entity.Car;
import com.example.carrental.entity.CarImage;
import com.example.carrental.exception.BadRequestException;
import com.example.carrental.exception.ResourceNotFoundException;
import com.example.carrental.mapper.CarImageMapper;
import com.example.carrental.repository.CarImageRepository;
import com.example.carrental.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarImageService {

    private final CarRepository carRepository;
    private final CarImageRepository carImageRepository;
    private final CarImageMapper carImageMapper;

    public List<CarImageResponseDto> getCarImages(Long carId) {
        ensureCarExists(carId);
        return carImageRepository.findAllByCarId(carId).stream().map(carImageMapper::toDto).toList();
    }

    public CarImageResponseDto addImage(Long carId, CarImageRequestDto requestDto) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));

        byte[] fileContent;
        try {
            fileContent = Base64.getDecoder().decode(requestDto.getBase64Content());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid base64 image content");
        }

        CarImage image = new CarImage();
        image.setFileName(requestDto.getFileName());
        image.setFileType(requestDto.getFileType());
        image.setFileContent(fileContent);
        image.setCar(car);

        return carImageMapper.toDto(carImageRepository.save(image));
    }

    public void deleteImage(Long carId, Long imageId) {
        ensureCarExists(carId);
        CarImage image = carImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));
        if (!image.getCar().getId().equals(carId)) {
            throw new BadRequestException("Image does not belong to this car");
        }
        carImageRepository.delete(image);
    }

    private void ensureCarExists(Long carId) {
        if (!carRepository.existsById(carId)) {
            throw new ResourceNotFoundException("Car not found");
        }
    }
}
