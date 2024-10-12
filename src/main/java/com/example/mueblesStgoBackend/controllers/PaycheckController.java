package com.example.mueblesStgoBackend.controllers;

import com.example.mueblesStgoBackend.entities.PaycheckEntity;
import com.example.mueblesStgoBackend.services.PaycheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/paycheck")
@CrossOrigin("*")
public class PaycheckController {

    @Autowired
    private PaycheckService paycheckService;

    @PostMapping("create")
    public PaycheckEntity createPaycheck(
            @RequestParam("rut") String rut,
            @RequestParam("year") int year,
            @RequestParam("month") int month) {
        return paycheckService.calculatePaycheck(rut, year, month);
    }

    @GetMapping("/{id}")
    public Optional<PaycheckEntity> getPaycheck(@PathVariable("id") Long id) {
        return paycheckService.getById(id);
    }

    @GetMapping("/getAll")
    public List<PaycheckEntity> getAllPaychecks() {
        return paycheckService.getAll();
    }

    @GetMapping("/dateFilter")
    public List<PaycheckEntity> getByYearAndMonth(@RequestParam("year") int year, @RequestParam("month") int month) {
        return paycheckService.getByYearAndMonth(year, month);
    }

    @GetMapping("/rutFilter")
    public List<PaycheckEntity> getByRut(@RequestParam("rut") String rut) {
        return paycheckService.getAllByRut(rut);
    }
}