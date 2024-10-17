package com.example.mueblesStgoBackend.services;

import com.example.mueblesStgoBackend.entities.ExtraHoursEntity;
import com.example.mueblesStgoBackend.entities.EmployeeEntity;
import com.example.mueblesStgoBackend.entities.CategoryEntity;
import com.example.mueblesStgoBackend.repositories.ExtraHoursRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExtraHoursServiceTest {

    @Mock
    private ExtraHoursRepository extraHoursRepository;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    ExtraHoursService extraHoursService = new ExtraHoursService();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Testint calculateExtraHours
     */
    @Test
    void whenCalculatingExtraHours_thenCreatesExtraHoursEntity() {
        // Given
        String rut = "12.345.678-9";
        Date date = Date.valueOf("2024-10-01");
        Time time = Time.valueOf("08:00:00");
        long extraHoursMinutes = 130; // 2 horas y 10 minutos

        EmployeeEntity employee = new EmployeeEntity();
        employee.setCategory("Engineer");

        CategoryEntity category = new CategoryEntity();
        category.setExtraHourRate(1500);

        when(employeeService.findByRut(rut)).thenReturn(employee);
        when(categoryService.findByCategory(employee.getCategory())).thenReturn(category);

        // When
        extraHoursService.calculateExtraHours(rut, date, time, extraHoursMinutes);

        // Then
        verify(extraHoursRepository, times(1)).save(any(ExtraHoursEntity.class));
    }

    /**
     * Testing calculateMonthlyExtraHoursPayment
     */
    @Test
    void whenCalculatingMonthlyExtraHoursPayment_thenReturnsCorrectAmount() {
        // Given
        String rut = "12.345.678-9";
        int year = 2024;
        int month = 10;
        int expectedPayment = 30000;

        when(extraHoursRepository.extraHoursPayment(rut, year, month)).thenReturn(expectedPayment);

        // When
        int actualPayment = extraHoursService.calculateMonthlyExtraHoursPayment(rut, year, month);

        // Then
        assertEquals(expectedPayment, actualPayment);
    }

    /**
     * Testing payExtraHours
     */
    @Test
    void whenPayingExtraHours_thenMarksAsPaid() {
        // Given
        String rut = "12.345.678-9";
        Date date = Date.valueOf("2024-10-01");
        ExtraHoursEntity extraHoursEntity = new ExtraHoursEntity();
        extraHoursEntity.setRut(rut);
        extraHoursEntity.setDate(date);
        extraHoursEntity.setPaid(false);

        when(extraHoursRepository.findAuthorizedByRutAndDate(rut, date)).thenReturn(extraHoursEntity);

        // When
        extraHoursService.payExtraHours(rut, date);

        // Then
        verify(extraHoursRepository, times(1)).save(extraHoursEntity);
        assertThat(extraHoursEntity.isPaid()).isTrue();
    }

    /**
     * Testing payAllAuthorizedExtraHours
     */
    @Test
    void whenPayingAllAuthorizedExtraHours_thenPaysEachExtraHour() {
        // Given
        int year = 2024;
        int month = 10;

        // Creación de horas extra
        ExtraHoursEntity extraHours1 = new ExtraHoursEntity();
        extraHours1.setRut("12.345.678-9");
        extraHours1.setDate(Date.valueOf("2024-10-01"));
        extraHours1.setPaid(false);

        ExtraHoursEntity extraHours2 = new ExtraHoursEntity();
        extraHours2.setRut("98.765.432-1");
        extraHours2.setDate(Date.valueOf("2024-10-02"));
        extraHours2.setPaid(false);

        // Mock de repositorio para retornar una lista con las horas extras
        when(extraHoursRepository.findAllAuthorizedByYearAndMonth(year, month))
                .thenReturn(List.of(extraHours1, extraHours2));

        // Mock de payExtraHours para retornar las horas extras específicas requeridas
        when(extraHoursRepository.findAuthorizedByRutAndDate(extraHours1.getRut(), extraHours1.getDate()))
                .thenReturn(extraHours1);
        when(extraHoursRepository.findAuthorizedByRutAndDate(extraHours2.getRut(), extraHours2.getDate()))
                .thenReturn(extraHours2);

        // When
        extraHoursService.payAllAuthorizedExtraHours(year, month);

        // Then
        verify(extraHoursRepository, times(2)).save(any(ExtraHoursEntity.class));
        assertThat(extraHours1.isPaid()).isTrue();
        assertThat(extraHours2.isPaid()).isTrue();
    }

    /**
     * Testing getAllByYearAndMonth
     */
    @Test
    void whenGettingAllByYearAndMonth_thenReturnsExtraHours() {
        // Given
        int year = 2024;
        int month = 10;

        ExtraHoursEntity extraHours1 = new ExtraHoursEntity();
        extraHours1.setRut("12.345.678-9");
        extraHours1.setDate(Date.valueOf("2024-10-01"));

        ExtraHoursEntity extraHours2 = new ExtraHoursEntity();
        extraHours2.setRut("98.765.432-1");
        extraHours2.setDate(Date.valueOf("2024-10-02"));

        // Mock the repository response
        when(extraHoursRepository.findAllByYearAndMonth(year, month))
                .thenReturn(List.of(extraHours1, extraHours2));

        // When
        List<ExtraHoursEntity> result = extraHoursService.getAllByYearAndMonth(year, month);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(extraHours1, extraHours2);
    }

    /**
     * Testing getByRutAndYearAndMonth
     */

    @Test
    void whenGettingByRutAndYearAndMonth_thenReturnsExtraHours() {
        // Given
        String rut = "12.345.678-9";
        int year = 2024;
        int month = 10;

        ExtraHoursEntity expectedExtraHours = new ExtraHoursEntity();
        expectedExtraHours.setRut(rut);
        expectedExtraHours.setDate(Date.valueOf("2024-10-01"));
        expectedExtraHours.setId(1L);

        when(extraHoursRepository.findByRutAndYearAndMonth(expectedExtraHours.getId(), rut, year, month))
                .thenReturn(expectedExtraHours);

        // When
        ExtraHoursEntity result = extraHoursService.getByRutAndYearAndMonth(expectedExtraHours.getId(), rut, year, month);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRut()).isEqualTo(rut);
        assertThat(result.getDate()).isEqualTo(Date.valueOf("2024-10-01"));
    }

    /**
     * Testing updateAuthorization
     */
    @Test
    void whenUpdatingAuthorization_thenSavesUpdatedExtraHours() {
        // Given
        ExtraHoursEntity extraHours = new ExtraHoursEntity();
        extraHours.setRut("12.345.678-9");
        extraHours.setAuthorized(false);

        // When
        extraHoursService.updateAuthorization(extraHours, true);

        // Then
        verify(extraHoursRepository, times(1)).save(extraHours);
        assertThat(extraHours.isAuthorized()).isTrue();
    }

}
