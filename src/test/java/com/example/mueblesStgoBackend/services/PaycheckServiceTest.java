package com.example.mueblesStgoBackend.services;

import com.example.mueblesStgoBackend.entities.CategoryEntity;
import com.example.mueblesStgoBackend.entities.EmployeeEntity;
import com.example.mueblesStgoBackend.entities.PaycheckEntity;
import com.example.mueblesStgoBackend.repositories.PaycheckRepository;
import com.example.mueblesStgoBackend.repositories.ServiceBonusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PaycheckServiceTest {

    @Mock
    private PaycheckRepository paycheckRepository;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private ServiceBonusRepository serviceBonusRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private DiscountService discountService;

    @Mock
    private ExtraHoursService extraHoursService;

    @InjectMocks
    PaycheckService paycheckServiceTest = new PaycheckService();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests for getById
     **/
    @Test
    void whenPaycheckExists_thenReturnPaycheck() {
        // Given
        Long id = 1L;
        PaycheckEntity mockPaycheck = new PaycheckEntity();
        when(paycheckRepository.findById(id)).thenReturn(Optional.of(mockPaycheck));

        // When
        Optional<PaycheckEntity> result = paycheckServiceTest.getById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(mockPaycheck);
    }

    @Test
    void whenPaycheckDoesNotExist_thenReturnEmpty() {
        // Given
        Long id = 1L;
        when(paycheckRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<PaycheckEntity> result = paycheckServiceTest.getById(id);

        // Then
        assertThat(result).isNotPresent();
    }

    /**
     * Testing getAll
     **/
    @Test
    void whenGetAllPaychecks_thenReturnListOfPaychecks() {
        // Given
        List<PaycheckEntity> mockPaychecks = new ArrayList<>();
        mockPaychecks.add(new PaycheckEntity());
        mockPaychecks.add(new PaycheckEntity());
        when(paycheckRepository.findAll()).thenReturn(mockPaychecks);

        // When
        List<PaycheckEntity> result = paycheckServiceTest.getAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(mockPaychecks);
    }

    @Test
    void whenNoPaychecksExist_thenReturnEmptyList() {
        // Given
        when(paycheckRepository.findAll()).thenReturn(new ArrayList<>());

        // When
        List<PaycheckEntity> result = paycheckServiceTest.getAll();

        // Then
        assertThat(result).isEmpty();
    }

    /**
     * Testing getByYearAndMonth
     **/
    @Test
    void whenGetPaychecksByYearAndMonth_thenReturnListOfPaychecks() {
        // Given
        int year = 2023;
        int month = 10;
        List<PaycheckEntity> mockPaychecks = new ArrayList<>();
        mockPaychecks.add(new PaycheckEntity());
        when(paycheckRepository.getByYearAndMonth(year, month)).thenReturn(mockPaychecks);

        // When
        List<PaycheckEntity> result = paycheckServiceTest.getByYearAndMonth(year, month);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(mockPaychecks);
    }

    @Test
    void whenNoPaychecksFoundByYearAndMonth_thenReturnEmptyList() {
        // Given
        int year = 2023;
        int month = 10;
        when(paycheckRepository.getByYearAndMonth(year, month)).thenReturn(new ArrayList<>());

        // When
        List<PaycheckEntity> result = paycheckServiceTest.getByYearAndMonth(year, month);

        // Then
        assertThat(result).isEmpty();
    }

    /**
     * Testing getAllByRut
     **/
    @Test
    void whenGetPaychecksByRut_thenReturnListOfPaychecks() {
        // Given
        String rut = "12.345.678-9";
        List<PaycheckEntity> mockPaychecks = new ArrayList<>();
        mockPaychecks.add(new PaycheckEntity());
        when(paycheckRepository.findAllByRut(rut)).thenReturn(mockPaychecks);

        // When
        List<PaycheckEntity> result = paycheckServiceTest.getAllByRut(rut);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(mockPaychecks);
    }

    @Test
    void whenNoPaychecksFoundByRut_thenReturnEmptyList() {
        // Given
        String rut = "12.345.678-9";
        when(paycheckRepository.findAllByRut(rut)).thenReturn(new ArrayList<>());

        // When
        List<PaycheckEntity> result = paycheckServiceTest.getAllByRut(rut);

        // Then
        assertThat(result).isEmpty();
    }


    /**
     * Testing calculatePaycheck
     **/
    @Test
    void whenCalculatingPaycheck_thenReturnsCorrectPaycheck() {
        // Given
        String rut = "11.234.123-6";
        int year = 2024;
        int month = 9;

        // Mocking de datos del empleado
        EmployeeEntity employee = new EmployeeEntity();
        employee.setRut(rut);
        employee.setNames("John");
        employee.setLastNames("Doe");
        employee.setCategory("A");
        employee.setStartDate(Date.valueOf("2014-10-01"));

        // Mock entidad categoría
        CategoryEntity category = new CategoryEntity(1L, "A", 25000, 1700000);

        // Mocking de búsqueda de empleado y de categoría
        when(employeeService.findByRut(rut)).thenReturn(employee);
        when(categoryService.findByCategory(employee.getCategory())).thenReturn(category);

        // Mock de información de años de servicio
        int serviceYears = 10;
        double serviceBonusPercentage = 10.0;
        when(serviceBonusRepository.getPercentageByServiceYears(serviceYears)).thenReturn((int) serviceBonusPercentage);

        // Mock bono por horas extra
        int extraHoursBonus = 50000;
        when(extraHoursService.calculateMonthlyExtraHoursPayment(rut, year, month)).thenReturn(extraHoursBonus);

        // Mock cálculo de descuentos totales
        int discounts = 30000;
        when(discountService.calculateTotalDiscount(rut, year, month, category.getMonthlySalary())).thenReturn(discounts);

        // Captura de PaycheckEntity que se guarda en el repositorio
        ArgumentCaptor<PaycheckEntity> paycheckCaptor = ArgumentCaptor.forClass(PaycheckEntity.class);
        when(paycheckRepository.save(any(PaycheckEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        PaycheckEntity paycheck = paycheckServiceTest.calculatePaycheck(rut, year, month);

        // Then
        verify(paycheckRepository).save(paycheckCaptor.capture());
        PaycheckEntity capturedPaycheck = paycheckCaptor.getValue();

        assertThat(capturedPaycheck.getRut()).isEqualTo(rut);
        assertThat(capturedPaycheck.getServiceYears()).isEqualTo(serviceYears);
        assertThat(capturedPaycheck.getMonthlyBaseSalary()).isEqualTo(category.getMonthlySalary());
        assertThat(capturedPaycheck.getServiceBonus()).isEqualTo((int) Math.floor(category.getMonthlySalary() * (serviceBonusPercentage / 100)));
        assertThat(capturedPaycheck.getExtraHoursBonus()).isEqualTo(extraHoursBonus);
        assertThat(capturedPaycheck.getDiscounts()).isEqualTo(discounts);

        // Calcula el sueldo bruto
        int serviceBonus = (int) Math.floor(category.getMonthlySalary() * (serviceBonusPercentage / 100));
        int grossSalary = category.getMonthlySalary() + serviceBonus + extraHoursBonus - discounts;
        assertThat(capturedPaycheck.getGrossSalary()).isEqualTo(grossSalary);

        // Calcula los descuentos
        int retirementDeduction = (int) Math.floor(grossSalary * 0.1);
        int healthDeduction = (int) Math.floor(grossSalary * 0.08);
        assertThat(capturedPaycheck.getRetirementDeduction()).isEqualTo(retirementDeduction);
        assertThat(capturedPaycheck.getHealthDeduction()).isEqualTo(healthDeduction);

        // Calcula el sueldo total final
        assertThat(capturedPaycheck.getTotalSalary()).isEqualTo(grossSalary - retirementDeduction - healthDeduction);

        // Verifica que el paycheck se guarda
        verify(paycheckRepository, times(1)).save(any(PaycheckEntity.class));
    }


}
