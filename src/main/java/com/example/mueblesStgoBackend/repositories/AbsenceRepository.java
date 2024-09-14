package com.example.mueblesStgoBackend.repositories;

import com.example.mueblesStgoBackend.entities.AbsenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AbsenceRepository extends JpaRepository<AbsenceEntity, Long> {
}
