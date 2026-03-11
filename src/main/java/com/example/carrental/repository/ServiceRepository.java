package com.example.carrental.repository;

import com.example.carrental.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}