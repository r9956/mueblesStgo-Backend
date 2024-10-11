package com.example.mueblesStgoBackend.repositories;

import com.example.mueblesStgoBackend.entities.AbsenceExcuseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface AbsenceExcuseRepository extends JpaRepository<AbsenceExcuseEntity, Long> {

    @Query(value="SELECT * FROM absence_excuse WHERE rut = :rut AND from_date = :fromDate AND to_date = :toDate", nativeQuery = true)
    AbsenceExcuseEntity findAbsenceExcuse(
            @Param("rut") String rut,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate);

    @Query(value="SELECT * FROM absence_excuse WHERE YEAR(fromDate) = :year AND MONTH(fromDate) = :month", nativeQuery = true)
    List<AbsenceExcuseEntity> findAllByYearAndMonth(
            @Param("year") int year,
            @Param("month") int month);
}