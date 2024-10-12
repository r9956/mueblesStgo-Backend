package com.example.mueblesStgoBackend.services;

import com.example.mueblesStgoBackend.entities.EmployeeEntity;
import com.example.mueblesStgoBackend.entities.PaycheckEntity;
import com.example.mueblesStgoBackend.entities.PayrollEntity;
import com.example.mueblesStgoBackend.repositories.PayrollRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PayrollServiceTest {

    @Mock
    private PayrollRepository payrollRepository;

    @Mock
    private ClockDataService clockDataService;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private PaycheckService paycheckService;

    @Mock
    private AbsenceExcuseService absenceExcuseService;

    @Mock
    private ExtraHoursService extraHoursService;

    @Mock
    private ExtraHoursAuthorizationService extraHoursAuthorizationService;

    @Mock
    private DiscountService discountService;

    @InjectMocks
    PayrollService payrollService = new PayrollService();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Testing generateMonthlyPayroll
     */
    @Test
    void whenPayrollAlreadyExists_thenResponseIsBAD_REQUEST() {
        // Given
        int year = 2024;
        int month = 10;

        when(clockDataService.isThereClockDataForYearAndMonth(year, month)).thenReturn(true);
        when(clockDataService.isThereUnprocessedDataForYearAndMonth(year, month)).thenReturn(false);

        // When
        ResponseEntity<String> response = payrollService.generateMonthlyPayroll(year, month);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Error: Ya existe un payroll generado para esta mes y año.");
    }

    @Test
    void whenNoClockDataExists_thenResponseIsBAD_REQUEST() {
        // Given
        int year = 2024;
        int month = 10;

        when(clockDataService.isThereClockDataForYearAndMonth(year, month)).thenReturn(false);

        // When
        ResponseEntity<String> response = payrollService.generateMonthlyPayroll(year, month);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Error: No existe información de reloj para el año y mes escogido.");
    }

    @Test
    void whenPayrollIsGeneratedSuccessfully_thenReturnsSuccessMessage() {
        // Given
        int year = 2024;
        int month = 10;

        when(clockDataService.isThereClockDataForYearAndMonth(year, month)).thenReturn(true);
        when(clockDataService.isThereUnprocessedDataForYearAndMonth(year, month)).thenReturn(true);
        when(payrollRepository.findByYearAndMonth(year, month)).thenReturn(null);

        List<EmployeeEntity> employees = new ArrayList<>();
        EmployeeEntity employee = new EmployeeEntity();
        employee.setRut("12.345.678-9");
        employees.add(employee);

        when(employeeService.getAll()).thenReturn(employees);
        when(employeeService.doesRutExists(anyString())).thenReturn(true);
        when(paycheckService.calculatePaycheck(anyString(), anyInt(), anyInt())).thenReturn(new PaycheckEntity());

        // When
        ResponseEntity<String> response = payrollService.generateMonthlyPayroll(year, month);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Payroll generado correctamente.");

        // Verifica que los métodos fueron llamados
        verify(clockDataService).analyzeClockData(year, month);
        verify(absenceExcuseService).applyExcuses(year, month);
        verify(extraHoursAuthorizationService).authorizeExtraHoursByYearAndMonth(year, month);
        verify(discountService).applyDiscounts(year, month);
        verify(extraHoursService).payAllAuthorizedExtraHours(year, month);
        verify(payrollRepository).save(any(PayrollEntity.class));
    }

    /**
     * Testing doesPayrollExists
     */
    @Test
    void whenPayrollExists_thenReturnsTrue() {
        // Given
        int year = 2024;
        int month = 10;
        PayrollEntity existingPayroll = new PayrollEntity();

        when(payrollRepository.findByYearAndMonth(year, month)).thenReturn(existingPayroll);

        // When
        boolean result = payrollService.doesPayrollExists(year, month);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void whenPayrollDoesNotExist_thenReturnsFalse() {
        // Given
        int year = 2024;
        int month = 10;

        when(payrollRepository.findByYearAndMonth(year, month)).thenReturn(null);

        // When
        boolean result = payrollService.doesPayrollExists(year, month);

        // Then
        assertThat(result).isFalse();
    }

}
