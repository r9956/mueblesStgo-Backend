package com.example.mueblesStgo_backend.repositories;

import com.example.mueblesStgo_backend.entities.ExtraHoursEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExtraHoursRepository extends JpaRepository<ExtraHoursEntity, Long> {
}
