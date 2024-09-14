package com.example.mueblesStgoBackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

@Entity
@Table(name = "extraHours")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtraHoursEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;
    private String rut;
    private Date date;
    private int numExtraHours;
    private int numExtraMin;
    private int numExtraSec;
}