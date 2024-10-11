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

        // Calcula los años de servicio
        int serviceYears = Math.toIntExact(ChronoUnit.YEARS.between(
                employee.getStartDate().toLocalDate(),
                LocalDate.now(ZoneId.of("America/Santiago"))
        ));

        // Calcula el porcentaje del bono por años de servicio
        double serviceBonusPercentage = serviceBonusRepository.getPercentageByServiceYears(serviceYears) / 100.0;

        // Obtiene el salario base del empleado
        int baseSalary = categoryService
                .findByCategory(employee.getCategory())
                .getMonthlySalary();

        // Calcula el monto del bono por años de servicio
        int serviceBonus = (int) Math.floor(baseSalary * serviceBonusPercentage);

        // Calcula el bono por horas extra
        int extraHoursBonus = extraHoursService.calculateMonthlyExtraHoursPayment(rut, year, month);

        // Calcula descuentos por atrasos y ausencias injustificadas
        int discounts = discountService.calculateTotalDiscount(rut, year, month, baseSalary);

        // Calcula el sueldo bruto
        int grossSalary = baseSalary + serviceBonus + extraHoursBonus - discounts;

        // Calcula descuentos por jubilación
        int retirementDeduction = (int) Math.floor(grossSalary * 0.1);

        // Calcula descuentos por salud
        int healthDeduction = (int) Math.floor(grossSalary * 0.08);

        // Genera el pago (paycheck)
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
