package com.example.mueblesStgoBackend.repositories;

import com.example.mueblesStgoBackend.entities.PayrollEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayrollRepository extends JpaRepository<PayrollEntity, Long> {
}
