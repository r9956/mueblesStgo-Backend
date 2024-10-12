package com.example.mueblesStgoBackend.services;

import com.example.mueblesStgoBackend.entities.DiscountEntity;
import com.example.mueblesStgoBackend.repositories.DiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

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
        discount.setMinutes(minutes);
        discount.setPercentage(discountPercentage);
        discount.setApplied(false);
        discountRepository.save(discount);
    }

    public void calculateDiscount(String rut, Date date, Time time, long minutes) {
        double discountPercentage = 0.00;

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
        }
    }

    public int calculateTotalDiscount(String rut, int year, int month, int baseSalary) {
        List<DiscountEntity> discounts = discountRepository.filterByRutYearAndMonth(rut, year, month);
        int totalDiscount = 0;

        // Late Arrival discounts
        for (DiscountEntity d : discounts) {
            totalDiscount = (int) Math.floor(totalDiscount + (baseSalary * d.getPercentage()));
        }

        // Unexcused absence discounts
        int totalAbsencesDiscount = absenceService.calculateUnexcusedAbsence(rut, year, month, baseSalary);

        return totalDiscount + totalAbsencesDiscount;
    }

    public DiscountEntity getDiscountByRutAndDate(String rut, Date date) {
        return discountRepository.findByRutAndDate(rut, date);
    }

    public void applyDiscount(String rut, Date date) {
        DiscountEntity discount = getDiscountByRutAndDate(rut, date);
        discount.setApplied(true);
        discountRepository.save(discount);
    }

    public void applyDiscounts(int year, int month) {
        List<DiscountEntity> monthlydiscounts = discountRepository.filterByYearAndMonth(year, month);
        for (DiscountEntity discount : monthlydiscounts) {
            if (!discount.isApplied()) {
                applyDiscount(discount.getRut(), discount.getDate());
            }
        }
    }
}
