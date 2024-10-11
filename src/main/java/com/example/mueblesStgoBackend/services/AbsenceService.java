package com.example.mueblesStgoBackend.services;

import com.example.mueblesStgoBackend.entities.AbsenceEntity;
import com.example.mueblesStgoBackend.repositories.AbsenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Date;
import java.util.List;

@Service
public class AbsenceService {

    @Autowired
    AbsenceRepository absenceRepository;

    public List<AbsenceEntity> getAll() {
        return absenceRepository.findAll();
    }

    public void createAbsence(String rut, Date date, long minutes) {
        AbsenceEntity absence = new AbsenceEntity();
        absence.setRut(rut);
        absence.setDate(date);
        absence.setMinutes(minutes);
        absence.setExcused(false);
        absenceRepository.save(absence);
    }

    public AbsenceEntity findAbsenceByRutAndDate(String rut, Date date) {
        return absenceRepository.findAbsence(rut, date);
    }

    public List<AbsenceEntity> findAllUnexcusedByYearAndMonth(int year, int month) {
        return absenceRepository.filterAllUnexcusedByYearAndMonth(year, month);
    }

    public int calculateUnexcusedAbsence(String rut, int year, int month, int baseSalary) {
        List<AbsenceEntity> absences = absenceRepository.filterUnexcusedByRutYearAndMonth(rut, year, month);
        int totalDiscount = 0;

        // Unexcused absence discounts
        for (AbsenceEntity a : absences) {
            totalDiscount = (int) Math.floor(totalDiscount + (baseSalary * 0.15));
        }

        return totalDiscount;
    }
}
