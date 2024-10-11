package com.example.mueblesStgoBackend.controllers;

import com.example.mueblesStgoBackend.services.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payroll")
@CrossOrigin("*")
public class PayrollController {
    @Autowired
    private PayrollService payrollService;

    @PostMapping("/generate")
    public ResponseEntity<String> generatePayroll(@RequestParam("year") int year, @RequestParam("month") int month) {
        return payrollService.generateMonthlyPayroll(year, month);
    }
}
