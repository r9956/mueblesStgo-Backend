package com.example.mueblesStgoBackend.repositories;

import com.example.mueblesStgoBackend.entities.ExtraHoursEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExtraHoursRepository extends JpaRepository<ExtraHoursEntity, Long> {
    @Query(value = "SELECT * FROM extra_hours WHERE rut = :rut AND YEAR(date) = :year AND MONTH(date) = :month", nativeQuery = true)
    List<ExtraHoursEntity> filterByRutYearAndMonth(@Param("rut") String rut,
                                                   @Param("year") int year,
                                                   @Param ("month") int month);

    @Query(value = "SELECT COALESCE(SUM(extra_hours_payment), 0) FROM extra_hours WHERE authorized = true AND rut = :rut AND YEAR(date) = :year AND MONTH(date) = :month", nativeQuery = true)
    int extraHoursPayment(@Param("rut") String rut,
                          @Param("year") int year,
                          @Param ("month") int month);

}
