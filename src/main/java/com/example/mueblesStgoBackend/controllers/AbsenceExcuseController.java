package com.example.mueblesStgoBackend.controllers;

import com.example.mueblesStgoBackend.services.AbsenceExcuseService;
import com.example.mueblesStgoBackend.services.DateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;

@RestController
@RequestMapping("/excuse")
public class AbsenceExcuseController {

    @Autowired
    AbsenceExcuseService absenceExcuseService;

    @Autowired
    DateService dateService;

    @PostMapping("/add")
    public ResponseEntity<String> addExcuse(
            @RequestParam("rut") String rut,
            @RequestParam("fromDate") Date fromDate,
            @RequestParam("toDate") Date toDate,
            @RequestParam("file") MultipartFile file) {
        //System.out.println(dateService.calculateDaysBetween(fromDate, toDate));
        return absenceExcuseService.addExcuse(rut, fromDate, toDate, file);
    }
}
