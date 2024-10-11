package com.example.mueblesStgoBackend.services;

import com.example.mueblesStgoBackend.entities.AbsenceEntity;
import com.example.mueblesStgoBackend.entities.AbsenceExcuseEntity;
import com.example.mueblesStgoBackend.repositories.AbsenceExcuseRepository;
import com.example.mueblesStgoBackend.repositories.AbsenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.util.List;

@Service
public class AbsenceExcuseService {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DateService dateService;

    @Autowired
    private AbsenceService absenceService;

    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private AbsenceExcuseRepository absenceExcuseRepository;

    private boolean validateAbsenceDates(Date fromDate, Date toDate) {
        return dateService.isDateRangeValid(fromDate, toDate);
    }

    public void saveAbsenceExcuse(String rut, Date fromDate, Date toDate, MultipartFile file) {
        AbsenceExcuseEntity absenceExcuse = new AbsenceExcuseEntity();
        absenceExcuse.setRut(rut);
        absenceExcuse.setFromDate(fromDate);
        absenceExcuse.setToDate(toDate);
        //file

        absenceExcuseRepository.save(absenceExcuse);
    }

    public boolean doesAbsenceExcuseExists(String rut, Date fromDate, Date toDate) {
        return absenceExcuseRepository.findAbsenceExcuse(rut, fromDate, toDate) != null;
    }

    public void applyExcuses(int year, int month) {
        List<AbsenceEntity> absences = absenceService.findAllUnexcusedByYearAndMonth(year, month);

        for (AbsenceEntity absence : absences) {
            List<AbsenceExcuseEntity> excuses = absenceExcuseRepository.findAllByRutAndYearAndMonth(absence.getRut(), year, month);

            for (AbsenceExcuseEntity excuse : excuses) {
                if (isDateWithinRange(absence.getDate(), excuse.getFromDate(), excuse.getToDate())) {
                    excuseAbsence(absence);
                }
            }
        }
    }

    public void excuseAbsence(AbsenceEntity absence) {
        if (!absence.isExcused()) {
            absence.setExcused(true);
            absenceRepository.save(absence);
        }
    }

    private boolean isDateWithinRange(Date absenceDate, Date fromDate, Date toDate) {
        return (absenceDate.equals(fromDate) || absenceDate.equals(toDate)) ||
                (absenceDate.after(fromDate) && absenceDate.before(toDate));
    }

    public ResponseEntity<String> addExcuse(String rut, Date fromDate, Date toDate, MultipartFile file) {
        if (doesAbsenceExcuseExists(rut, fromDate, toDate)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Una justificación para este empleado y fecha ya ha sido ingresada previamente.");
        }
        if (employeeService.rutFormatValidation(rut)) {
            if (employeeService.doesRutExists(rut)) {
                if (validateAbsenceDates(fromDate, toDate)) {
                    saveAbsenceExcuse(rut, fromDate, toDate, file);
                    return ResponseEntity.status(HttpStatus.OK).body("Justificación de ausencia ingresada correctamente.");
                }
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: El rango de fechas no es válido.");
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: No hay un empleado registrado con el RUT: " + rut + ".");
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: El rut " + rut + " no es válido.");
    }
}
