package com.example.mueblesStgoBackend.services;

import com.example.mueblesStgoBackend.entities.ExtraHoursAuthorizationEntity;
import com.example.mueblesStgoBackend.repositories.ExtraHoursAuthorizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class ExtraHoursAuthorizationService {

    @Autowired
    ExtraHoursAuthorizationRepository extraHoursAuthorizationRepository;

    public ExtraHoursAuthorizationEntity findAuthorization(String rut, Date date) {
        return extraHoursAuthorizationRepository.findByRutAndDate(rut, date);
    }

    public ResponseEntity<String> addAuthorization(ExtraHoursAuthorizationEntity auth) {
        if (findAuthorization(auth.getRut(), auth.getDate()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: An authorization for this person and date has already been submitted.");
        }
        else {
            extraHoursAuthorizationRepository.save(auth);
            return ResponseEntity.ok("Authorization submitted successfully.");
        }
    }
}
