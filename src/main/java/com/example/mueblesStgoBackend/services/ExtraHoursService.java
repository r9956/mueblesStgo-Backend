package com.example.mueblesStgoBackend.services;

import com.example.mueblesStgoBackend.entities.ExtraHoursEntity;
import com.example.mueblesStgoBackend.repositories.ExtraHoursRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;

@Service
public class ExtraHoursService {

    @Autowired
    private ExtraHoursRepository extraHoursRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CategoryService categoryService;

    public void calculateExtraHours(String rut, Date date, Time time, long extraHoursMinutes) {
        int extraHoursRate = categoryService.findByCategory(employeeService.findByRut(rut).getCategory()).getExtraHourRate();
        double ratePerMinute = extraHoursRate / 60.0;

        // Calculate total extra hours and remaining minutes
        int extraHours = (int) (extraHoursMinutes / 60); // Get full hours
        int remainingMinutes = (int) extraHoursMinutes % 60;  // Get remaining minutes

        // Calculate the payment, truncating decimals
        int extraHoursPayment = (int) Math.floor(extraHoursMinutes * ratePerMinute);

        // Call to createExtraHours
        createExtraHours(rut, date, time, extraHours, remainingMinutes, extraHoursPayment);
    }


    private void createExtraHours(String rut, Date date, Time time, int numHours, int numMinutes, int extraHoursPayment) {
        ExtraHoursEntity extraHours = new ExtraHoursEntity();
        extraHours.setRut(rut);
        extraHours.setDate(date);
        extraHours.setTime(time);
        extraHours.setNumExtraHours(numHours);
        extraHours.setNumExtraMin(numMinutes);
        extraHours.setNumExtraSec(0);
        extraHours.setExtraHoursPayment(extraHoursPayment);
        extraHours.setAuthorized(false);
        extraHoursRepository.save(extraHours);
    }
}
