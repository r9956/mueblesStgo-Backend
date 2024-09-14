package com.example.mueblesStgoBackend.repositories;

import com.example.mueblesStgoBackend.entities.EntryTimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

@Repository
public interface EntryTimeRepository extends JpaRepository<EntryTimeEntity, Long> {

    @Procedure("LoadTimeEntries")
    void loadClock(String filePath);
}