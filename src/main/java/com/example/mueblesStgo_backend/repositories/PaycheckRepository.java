package com.example.mueblesStgo_backend.repositories;

import com.example.mueblesStgo_backend.entities.PaycheckEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaycheckRepository extends JpaRepository<PaycheckEntity, Long> {
}
