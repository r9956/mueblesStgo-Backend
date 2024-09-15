package com.example.mueblesStgoBackend.repositories;

import com.example.mueblesStgoBackend.entities.DiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<DiscountEntity, Long> {

    @Query(value = "SELECT * FROM discount WHERE applied = false AND rut = :rut AND YEAR(date) = :year AND MONTH(date) = :month; \n", nativeQuery = true)
    List<DiscountEntity> filterByRutYearAndMonth(@Param("rut") String rut,
                                                 @Param("year") int year,
                                                 @Param("month") int month);
}
