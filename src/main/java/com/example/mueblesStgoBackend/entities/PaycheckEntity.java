package com.example.mueblesStgoBackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "paychecks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaycheckEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;
    private String rut;
    private String name;
    private String category;
    private int year;
    private int month;
    private int serviceYears;
    private int monthlyBaseSalary;
    private int extraHoursBonus;
    private int serviceBonus;
    private int discounts;
    private int grossSalary;
    private int retirementDeduction;
    private int healthDeduction;
    private int totalSalary;
}