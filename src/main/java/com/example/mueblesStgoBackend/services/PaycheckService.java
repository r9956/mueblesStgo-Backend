package com.example.mueblesStgoBackend.services;

import com.example.mueblesStgoBackend.entities.EmployeeEntity;
import com.example.mueblesStgoBackend.entities.PaycheckEntity;
import com.example.mueblesStgoBackend.repositories.PaycheckRepository;
import com.example.mueblesStgoBackend.repositories.ServiceBonusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Service
public class PaycheckService {
    @Autowired
    PaycheckRepository paycheckRepository;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ServiceBonusRepository serviceBonusRepository;

    @Autowired
    ExtraHoursService extraHoursService;

    @Autowired
    DiscountService discountService;

    public PaycheckEntity calculatePaycheck(String rut, int year, int month) {
        EmployeeEntity employee = employeeService.findByRut(rut);

        // Calculate service years
        int serviceYears = Math.toIntExact(ChronoUnit.YEARS.between(
                employee.getStartDate().toLocalDate(),
                LocalDate.now(ZoneId.of("America/Santiago"))
        ));

        // Calculate service bonus percentage
        double serviceBonusPercentage = serviceBonusRepository.getPercentageByServiceYears(serviceYears) / 100.0;

        // Get employee's base salary
        int baseSalary = categoryService
                .findByCategory(employee.getCategory())
                .getMonthlySalary();

        // Calculate service bonus payment
        int serviceBonus = (int) Math.floor(baseSalary * serviceBonusPercentage);

        // Calculate extra hours bonus
        int extraHoursBonus = extraHoursService.calculateMonthlyExtraHoursPayment(rut, year, month);

        // Calculate late arrivals and unexcused absences discounts
        int discounts = discountService.calculateTotalDiscount(rut, year, month, baseSalary);

        // Calculate gross salary
        int grossSalary = baseSalary + serviceBonus + extraHoursBonus - discounts;

        // Calculate retirement deductions
        int retirementDeduction = (int) Math.floor(grossSalary * 0.1);

        // Calculate health insurance deduction
        int healthDeduction = (int) Math.floor(grossSalary * 0.08);

        // Generates paycheck
        PaycheckEntity paycheck = new PaycheckEntity();
        paycheck.setRut(rut);
        paycheck.setName(employee.getLastNames() + " " + employee.getNames());
        paycheck.setCategory(employee.getCategory());
        paycheck.setServiceYears(serviceYears);
        paycheck.setYear(year);
        paycheck.setMonth(month);
        paycheck.setMonthlyBaseSalary(baseSalary);
        paycheck.setServiceBonus(serviceBonus);
        paycheck.setExtraHoursBonus(extraHoursBonus);
        paycheck.setDiscounts(discounts);
        paycheck.setGrossSalary(grossSalary);
        paycheck.setRetirementDeduction(retirementDeduction);
        paycheck.setHealthDeduction(healthDeduction);
        paycheck.setTotalSalary(grossSalary - retirementDeduction - healthDeduction);
        return paycheckRepository.save(paycheck);
    }


}
