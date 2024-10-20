package com.example.mueblesStgoBackend.repositories;

import com.example.mueblesStgoBackend.entities.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {
    @Query(value = "SELECT * FROM employees WHERE employees.rut = :rut", nativeQuery = true)
    EmployeeEntity findByRut(@Param("rut") String rut);
}