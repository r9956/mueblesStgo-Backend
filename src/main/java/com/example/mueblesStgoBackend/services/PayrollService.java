package com.example.mueblesStgoBackend.services;

import com.example.mueblesStgoBackend.entities.*;
import com.example.mueblesStgoBackend.repositories.PayrollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayrollService {
    @Autowired
    private PayrollRepository payrollRepository;

    @Autowired
    private ClockDataService clockDataService;

    @Autowired
    private AbsenceExcuseService absenceExcuseService;

    @Autowired
    private ExtraHoursAuthorizationService extraHoursAuthorizationService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PaycheckService paycheckService;

    @Autowired
    private DiscountService discountService;

    @Autowired
    private ExtraHoursService extraHoursService;

    public ResponseEntity<String> generateMonthlyPayroll(int year, int month) {

        if (clockDataService.isThereClockDataForYearAndMonth(year, month)) {
            if (clockDataService.isThereUnprocessedDataForYearAndMonth(year, month) && !doesPayrollExists(year, month)) {
                // Procesa la información obtenida del reloj
                clockDataService.analyzeClockData(year, month);

                // Busca y aplica los justificativos para las inasistencias sin justificar del año y mes elegido
                absenceExcuseService.applyExcuses(year, month);

                // Busca y autoriza las horas extras sin autorizar para el año y mes elegido
                extraHoursAuthorizationService.authorizeExtraHoursByYearAndMonth(year, month);

                // Obtiene a todos los empleados
                List<EmployeeEntity> employees = employeeService.getAll();

                // Genera el cálculo del pago mensual para cada empleado
                for (EmployeeEntity e : employees) {
                    if (employeeService.doesRutExists(e.getRut())) {
                        paycheckService.calculatePaycheck(e.getRut(), year, month);
                    }
                }

                // Actualiza la aplicación de los descuentos
                discountService.applyDiscounts(year, month);

                // Actualiza el pago de las horas extras autorizadas
                extraHoursService.payAllAuthorizedExtraHours(year, month);

                // Guarda el payroll realizado
                PayrollEntity newPayroll = new PayrollEntity();
                newPayroll.setYear(year);
                newPayroll.setMonth(month);
                payrollRepository.save(newPayroll);

                return ResponseEntity.ok().body("Payroll generado correctamente.");
            }
            return ResponseEntity.badRequest().body("Error: Ya existe un payroll generado para esta mes y año.");
        }
        else {
            return ResponseEntity.badRequest().body("Error: No existe información de reloj para el año y mes escogido.");
        }
    }

    public boolean doesPayrollExists(int year, int month) {
        return payrollRepository.findByYearAndMonth(year, month) != null;
    }

}
