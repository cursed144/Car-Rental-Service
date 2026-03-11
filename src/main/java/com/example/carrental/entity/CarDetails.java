package com.example.carrental.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "car_details")
@Getter
@Setter
@NoArgsConstructor
public class CarDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int mileage;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private int seats;

    @Column(nullable = false)
    private String transmission;

    @OneToOne
    @JoinColumn(name = "car_id", nullable = false, unique = true)
    private Car car;
}