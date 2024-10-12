package com.example.mueblesStgoBackend.repositories;

import com.example.mueblesStgoBackend.entities.PaycheckEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaycheckRepository extends JpaRepository<PaycheckEntity, Long> {

    @Query(value = "SELECT * FROM paychecks WHERE year = :year AND month = :month", nativeQuery = true)
    List<PaycheckEntity> getByYearAndMonth(
            @Param("year") int year,
            @Param("month") int month);

    @Query(value = "SELECT * FROM paychecks WHERE rut = :rut", nativeQuery = true)
    List<PaycheckEntity> findAllByRut(
            @Param("rut") String rut);
}
