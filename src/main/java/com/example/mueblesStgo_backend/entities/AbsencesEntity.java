package com.example.mueblesStgo_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity
@Table(name = "abcenses")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbsencesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;
    private String rut;
    private Date date;
    private boolean excused;
}