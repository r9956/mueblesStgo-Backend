package com.example.mueblesStgoBackend.repositories;

import com.example.mueblesStgoBackend.entities.AbsenceEntity;
import com.example.mueblesStgoBackend.entities.ClockDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Repository
public interface ClockDataRepository extends JpaRepository<ClockDataEntity, Long> {
    @Query(value = "SELECT * FROM clock_data WHERE date = :date AND time = :time AND rut = :rut", nativeQuery = true)
    ClockDataEntity findEntry(
            @Param("date") Date date,
            @Param("time") Time time,
            @Param("rut") String rut);

    @Query(value = "SELECT * FROM clock_data WHERE YEAR(date) = :year AND MONTH(date) = :month;", nativeQuery = true)
    List<ClockDataEntity> findAllByYearAndMonth(
            @Param("year") int year,
            @Param("month") int month);

    @Query(value = "SELECT * FROM clock_data WHERE processed = false AND YEAR(date) = :year AND MONTH(date) = :month;", nativeQuery = true)
    List<ClockDataEntity> findAllUnprocessedByYearAndMonth(
            @Param("year") int year,
            @Param("month") int month);
}
