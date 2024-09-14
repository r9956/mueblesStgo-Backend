package com.example.mueblesStgoBackend.services;

import com.example.mueblesStgoBackend.entities.AbsenceEntity;
import com.example.mueblesStgoBackend.repositories.AbsenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Date;

@Service
public class AbsenceService {

    @Autowired
    AbsenceRepository absenceRepository;

    public void createAbsence(String rut, Date date, long minutes) {
        AbsenceEntity absence = new AbsenceEntity();
        absence.setRut(rut);
        absence.setDate(date);
        absence.setMinutes(minutes);
        absence.setExcused(false);
        absenceRepository.save(absence);
    }
}
