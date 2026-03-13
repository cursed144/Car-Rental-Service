package com.example.carrental.repository;

import com.example.carrental.entity.MaintenanceService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceServiceRepository extends JpaRepository<MaintenanceService, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    boolean existsByIdAndCarsIsNotEmpty(Long id);
}