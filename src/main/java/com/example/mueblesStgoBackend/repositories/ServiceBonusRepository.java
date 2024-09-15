package com.example.mueblesStgoBackend.repositories;

import com.example.mueblesStgoBackend.entities.ServiceBonusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceBonusRepository extends JpaRepository<ServiceBonusEntity, Long> {

    @Query(value = "SELECT percentage FROM service_bonus WHERE :serviceYears >= years ORDER BY percentage DESC LIMIT 1", nativeQuery = true)
    int getPercentageByServiceYears(@Param("serviceYears") int serviceYears);

}
