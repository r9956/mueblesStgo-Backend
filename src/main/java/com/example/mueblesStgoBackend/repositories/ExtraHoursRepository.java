package com.example.mueblesStgoBackend.repositories;

import com.example.mueblesStgoBackend.entities.ExtraHoursEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExtraHoursRepository extends JpaRepository<ExtraHoursEntity, Long> {
}
