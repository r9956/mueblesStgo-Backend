package com.example.mueblesStgoBackend.services;

import com.example.mueblesStgoBackend.entities.AbsenceExcuseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayrollService {

    @Autowired
    private AbsenceService absenceService;

    @Autowired
    private AbsenceExcuseService absenceExcuseService;

    public void generateMonthlyPayroll(int year, int month) {

        // Busca las inasistencias justificadas
        List<AbsenceExcuseEntity> excusedAbsences = absenceExcuseService.getAllByYearAndMonth(year, month);




        // Apply excuses

        // Look for extra hours authorizations

        // Apply extra hours authorizations

        // Get all workers

        // Generate paychecks for all the workers

        // Confirm and apply payment

        // Update absence discounts and late arrival discounts

        // Save payroll(id, year, month, totalPayments)

    }
}
