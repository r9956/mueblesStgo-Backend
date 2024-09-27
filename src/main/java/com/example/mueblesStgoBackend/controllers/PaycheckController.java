package com.example.mueblesStgoBackend.controllers;

import com.example.mueblesStgoBackend.entities.PaycheckEntity;
import com.example.mueblesStgoBackend.services.PaycheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paycheck")
@CrossOrigin("*")
public class PaycheckController {

    @Autowired
    PaycheckService paycheckService;

    @PostMapping("create")
    public PaycheckEntity createPaycheck(
            @RequestParam("rut") String rut,
            @RequestParam("year") int year,
            @RequestParam("month") int month) {
        return paycheckService.calculatePaycheck(rut, year, month);
    }
}