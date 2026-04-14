package com.example.carrental.repository;

import com.example.carrental.entity.Car;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"details", "services", "images"})
    List<Car> findAll();

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"details", "services", "images"})
    Optional<Car> findById(@NonNull Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Car c where c.id = :id")
    Optional<Car> findByIdForUpdate(@Param("id") Long id);
}