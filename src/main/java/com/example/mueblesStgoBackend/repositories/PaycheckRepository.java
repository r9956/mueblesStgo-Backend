package com.example.mueblesStgoBackend.repositories;

import com.example.mueblesStgoBackend.entities.PaycheckEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaycheckRepository extends JpaRepository<PaycheckEntity, Long> {
}
