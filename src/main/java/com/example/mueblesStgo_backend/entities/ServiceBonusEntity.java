package com.example.mueblesStgo_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "serviceBonus")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceBonusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;
    private int years;
    private int percentage;
}