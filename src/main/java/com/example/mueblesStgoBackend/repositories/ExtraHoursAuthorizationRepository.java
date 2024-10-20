package com.example.mueblesStgoBackend.repositories;

import com.example.mueblesStgoBackend.entities.ExtraHoursAuthorizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface ExtraHoursAuthorizationRepository extends JpaRepository<ExtraHoursAuthorizationEntity, Long> {

    @Query(value = "SELECT * FROM extra_hours_authorization WHERE rut = :rut AND date = :date", nativeQuery = true)
    ExtraHoursAuthorizationEntity findByRutAndDate(
            @Param("rut") String rut,
            @Param("date") Date date);

    @Query(value = "SELECT * FROM extra_hours_authorization WHERE YEAR(date) = :year AND MONTH(date) = :month", nativeQuery = true)
    List<ExtraHoursAuthorizationEntity> findAllByYearAndMonth(
            @Param("year") int year,
            @Param("month") int month);
}
