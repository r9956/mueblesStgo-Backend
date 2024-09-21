package com.example.mueblesStgoBackend.controllers;

import com.example.mueblesStgoBackend.entities.ExtraHoursAuthorizationEntity;
import com.example.mueblesStgoBackend.services.ExtraHoursAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;

@RestController
@CrossOrigin("*")
@RequestMapping("/auth")
public class ExtraHoursAuthorizationController {

    @Autowired
    ExtraHoursAuthorizationService extraHoursAuthorizationService;

    @PostMapping("/add")
    public ResponseEntity<String> addAuthorization(@RequestParam String rut,
                                                   @RequestParam Date date,
                                                   @RequestParam MultipartFile file) {
        ExtraHoursAuthorizationEntity auth = new ExtraHoursAuthorizationEntity();
        auth.setRut(rut);
        auth.setDate(date);
        return extraHoursAuthorizationService.addAuthorization(auth);
    }

}
