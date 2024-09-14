package com.example.mueblesStgoBackend.repositories;

import com.example.mueblesStgoBackend.entities.ClockDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Time;

@Repository
public interface ClockDataRepository extends JpaRepository<ClockDataEntity, Long> {
    @Query(value = "SELECT * FROM clock_data WHERE date = :date AND time = :time AND rut = :rut", nativeQuery = true)
    ClockDataEntity findEntry(
            @Param("date") Date date,
            @Param("time") Time time,
            @Param("rut") String rut);
}
