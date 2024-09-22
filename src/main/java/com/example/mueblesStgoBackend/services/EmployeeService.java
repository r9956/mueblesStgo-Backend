package com.example.mueblesStgoBackend.services;

import com.example.mueblesStgoBackend.entities.EmployeeEntity;
import com.example.mueblesStgoBackend.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.status;

@Service
public class EmployeeService {
    @Autowired
    EmployeeRepository employeeRepository;

    public boolean doesRutExists(String rut) {
        return employeeRepository.findByRut(rut) != null;
    }

    public EmployeeEntity findByRut(String rut) {
        return employeeRepository.findByRut(rut);
    }

    public boolean rutFormatValidation(String rut) {
        if (rut.length() < 11 || rut.length() > 12) {
            return false;
        }

        int dashCount = 0;
        int periodCount = 0;

        for (char c : rut.toCharArray()) {
            if (c == '-') {
                dashCount++;
            } else if (c == '.') {
                periodCount++;
            }
        }

        if (dashCount != 1 || periodCount != 2) {
            return false;
        }

        int length = rut.length();
        if (rut.charAt(length - 2) != '-' || rut.charAt(length - 6) != '.' || rut.charAt(length - 10) != '.') {
            return false;
        }

        for (int i = 0; i < length; i++) {
            char c = rut.charAt(i);
            if (c != '-' && c != '.' && !Character.isDigit(c) && c != 'k') {
                return false;
            }
        }

        return true;
    }

    public ResponseEntity<String> addEmployee(EmployeeEntity employee) {
        if (!doesRutExists(employee.getRut())) {
            if (rutFormatValidation(employee.getRut())) {
                employeeRepository.save(employee);
                return status(HttpStatus.OK).body("Employee created successfully.");
            }
            else {
                return status(HttpStatus.EXPECTATION_FAILED).body("The employee's rut format is not correct.");
            }
        }
        return status(HttpStatus.CONFLICT).body("The employee already exists in the database.");
    }


    public List<EmployeeEntity> getAll() {
        return employeeRepository.findAll();
    }

    public Optional<EmployeeEntity> getById(Long id) {
        return employeeRepository.findById(id);
    }

    public ResponseEntity<String> delete(Long id) {
        if (getById(id).isPresent()) {
            employeeRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Employee deleted successfully.");
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There's no employee registered with id " + id + ".");
        }
    }

    public ResponseEntity<String> updateEmployee(Long id, EmployeeEntity updatedEmployee) {
        Optional<EmployeeEntity> existingEmployeeOpt = employeeRepository.findById(id);

        if (existingEmployeeOpt.isPresent()) {
            EmployeeEntity existingEmployee = existingEmployeeOpt.get();

            existingEmployee.setRut(updatedEmployee.getRut());
            existingEmployee.setNames(updatedEmployee.getNames());
            existingEmployee.setLastNames(updatedEmployee.getLastNames());
            existingEmployee.setBirthDate(updatedEmployee.getBirthDate());
            existingEmployee.setCategory(updatedEmployee.getCategory());
            existingEmployee.setStartDate(updatedEmployee.getStartDate());

            employeeRepository.save(existingEmployee);
            return ResponseEntity.ok("Empleado creado correctamente");
        } else {
            return ResponseEntity.status(404).body("No se pudo encontrar el empleado.");
        }
    }

}
