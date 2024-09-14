package com.example.mueblesStgoBackend.controllers;

import com.example.mueblesStgoBackend.entities.EmployeeEntity;
import com.example.mueblesStgoBackend.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/add")
    public ResponseEntity<String> addEmployee(@RequestBody EmployeeEntity employee) {
        return employeeService.addEmployee(employee);
    }
}
