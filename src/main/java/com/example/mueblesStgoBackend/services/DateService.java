package com.example.mueblesStgoBackend.services;

import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class DateService {

    public long calculateDaysBetween(Date fromDate, Date toDate) {
        LocalDate start = LocalDate.parse(fromDate.toString());
        LocalDate end   = LocalDate.parse(toDate.toString());

        return ChronoUnit.DAYS.between(start, end);
    }

    public boolean isDateRangeValid(Date fromDate, Date toDate) {
        return calculateDaysBetween(fromDate, toDate) >= 0;
    }
}