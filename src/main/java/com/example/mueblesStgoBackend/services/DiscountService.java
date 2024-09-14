package com.example.mueblesStgoBackend.services;

import com.example.mueblesStgoBackend.entities.DiscountEntity;
import com.example.mueblesStgoBackend.repositories.DiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;

@Service
public class DiscountService {

    @Autowired
    DiscountRepository discountRepository;

    @Autowired
    AbsenceService absenceService;

    public void createDiscount(String rut, Date date, Time time, long minutes, double discountPercentage) {
        DiscountEntity discount = new DiscountEntity();
        discount.setRut(rut);
        discount.setDate(date);
        discount.setTime(time);
        discount.setMotive("Atraso");
        discount.setMinutes(minutes);
        discount.setPercentage(discountPercentage);
        discount.setApplied(false);
        discountRepository.save(discount);
    }

    public void calculateDiscount(String rut, Date date, Time time, long minutes) {
        double discountPercentage = 0.00;
        System.out.println("___ DISCOUNT SERVICE ___");
        // > 10 min: 1%
        if (minutes > 10 && minutes <= 25) {
            discountPercentage = 0.01;
        }

        // > 25 min: 3%
        if (minutes > 25 && minutes <= 45) {
            discountPercentage = 0.03;
        }

        // > 45 min: 6%
        if (minutes > 45 && minutes <= 70) {
            discountPercentage = 0.06;
        }

        if (minutes <= 70) {
            createDiscount(rut, date, time, minutes, discountPercentage);
        }

        if (minutes > 70) {
            // Crea una inasistencia
            absenceService.createAbsence(rut, date, minutes);
            System.out.println("Inasistencia creada");
        }
    }
}
