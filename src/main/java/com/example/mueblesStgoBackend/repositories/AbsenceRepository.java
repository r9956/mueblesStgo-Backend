package com.example.mueblesStgoBackend.repositories;

import com.example.mueblesStgoBackend.entities.AbsenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface AbsenceRepository extends JpaRepository<AbsenceEntity, Long> {

    @Query(value = "SELECT * FROM absence WHERE rut = :rut AND date = :date", nativeQuery = true)
    AbsenceEntity findAbsence(@Param("rut") String rut,
                              @Param("date") Date date);

    @Query(value = "SELECT * FROM absence WHERE excused = false AND discount_applied = false AND rut = :rut AND YEAR(date) = :year AND MONTH(date) = :month; \n", nativeQuery = true)
    List<AbsenceEntity> filterUnexcusedByRutYearAndMonth(@Param("rut") String rut,
                                                         @Param("year") int year,
                                                         @Param("month") int month);

    @Query(value = "SELECT * FROM absence WHERE excused = false AND discount_applied = false AND YEAR(date) = :year AND MONTH(date) = :month;", nativeQuery = true)
    List<AbsenceEntity> filterAllUnexcusedByYearAndMonth(@Param("year") int year,
                                                         @Param("month") int month);

}