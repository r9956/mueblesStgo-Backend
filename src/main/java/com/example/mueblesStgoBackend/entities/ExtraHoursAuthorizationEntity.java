package com.example.mueblesStgoBackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity
@Table(name = "extraHoursAuthorization")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtraHoursAuthorizationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;
    private String rut;
    private Date date;
}
