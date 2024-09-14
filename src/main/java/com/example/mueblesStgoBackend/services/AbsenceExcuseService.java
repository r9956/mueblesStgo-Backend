package com.example.mueblesStgoBackend.services;

import com.example.mueblesStgoBackend.entities.AbsenceExcuseEntity;
import com.example.mueblesStgoBackend.repositories.AbsenceExcuseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;

@Service
public class AbsenceExcuseService {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DateService dateService;

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

    public ResponseEntity<String> addExcuse(String rut, Date fromDate, Date toDate, MultipartFile file) {
        if (doesAbsenceExcuseExists(rut, fromDate, toDate)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: An excuse for this employee's absence has already been registered.");
        }
        if (employeeService.rutFormatValidation(rut)) {
            if (employeeService.doesRutExists(rut)) {
                if (validateAbsenceDates(fromDate, toDate)) {
                    saveAbsenceExcuse(rut, fromDate, toDate, file);
                    return ResponseEntity.status(HttpStatus.OK).body("Absence excuse added successfully.");
                }
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: The range between dates is not valid.");
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: There is not a registered employee with rut: " + rut + ".");
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Rut " + rut + " is not valid.");
    }
}
