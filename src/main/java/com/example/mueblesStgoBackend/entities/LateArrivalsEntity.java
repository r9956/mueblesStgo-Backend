package com.example.mueblesStgoBackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity
@Table(name = "lateArrivals")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LateArrivalsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;
    private String rut;
    private Date date;
    private int numHours;
    private int numMin;
    private int numSec;
    private boolean excused;
}