package com.example.carrental.repository;

import com.example.carrental.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    boolean existsByCarIdAndStartDateLessThanAndEndDateGreaterThan(
            Long carId,
            LocalDate endDate,
            LocalDate startDate
    );

    boolean existsByCarIdAndStartDateLessThanAndEndDateGreaterThanAndIdNot(
            Long carId,
            LocalDate endDate,
            LocalDate startDate,
            Long id
    );
}