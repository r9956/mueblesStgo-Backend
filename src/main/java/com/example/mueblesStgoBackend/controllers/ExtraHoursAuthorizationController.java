package com.example.mueblesStgoBackend.controllers;

import com.example.mueblesStgoBackend.entities.ExtraHoursAuthorizationEntity;
import com.example.mueblesStgoBackend.services.ExtraHoursAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class ExtraHoursAuthorizationController {

    @Autowired
    ExtraHoursAuthorizationService extraHoursAuthorizationService;

    @PostMapping("/add")
    public ResponseEntity<String> addAuthorization(@RequestBody ExtraHoursAuthorizationEntity auth) {
        return extraHoursAuthorizationService.addAuthorization(auth);
    }

}
