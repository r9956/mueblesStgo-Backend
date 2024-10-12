package com.example.mueblesStgoBackend.repositories;

import com.example.mueblesStgoBackend.entities.PayrollEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PayrollRepository extends JpaRepository<PayrollEntity, Long> {
    @Query(value = "SELECT * FROM payroll WHERE year = :year AND month = :month", nativeQuery = true)
    PayrollEntity findByYearAndMonth(
            @Param("year") int year,
            @Param("month") int month);
}
