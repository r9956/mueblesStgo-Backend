package com.example.mueblesStgo_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;

@Entity
@Table(name = "ClockData")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClockDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;
    private Date date;
    private Time time;
    private String rut;
}