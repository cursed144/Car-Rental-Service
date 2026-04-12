package com.example.carrental.repository;

import com.example.carrental.entity.CarImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarImageRepository extends JpaRepository<CarImage, Long> {
    List<CarImage> findAllByCarId(Long carId);
}
