package com.example.mueblesStgoBackend.services;

import com.example.mueblesStgoBackend.entities.DiscountEntity;
import com.example.mueblesStgoBackend.repositories.DiscountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class DiscountServiceTest {

    @Mock
    private DiscountRepository discountRepository;

    @Mock
    private AbsenceService absenceService;

    @InjectMocks
    private DiscountService discountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Testing createDiscount
     **/
    @Test
    void whenCreatingDiscount_thenDiscountIsSaved() {
        // Given
        String rut = "12.345.678-9";
        Date date = Date.valueOf("2024-08-17");
        Time time = Time.valueOf("08:15:00");
        long minutes = 15;
        double discountPercentage = 0.01;

        // When
        discountService.createDiscount(rut, date, time, minutes, discountPercentage);

        // Then
        DiscountEntity expectedDiscount = new DiscountEntity();
        expectedDiscount.setRut(rut);
        expectedDiscount.setDate(date);
        expectedDiscount.setTime(time);
        expectedDiscount.setMinutes(minutes);
        expectedDiscount.setPercentage(discountPercentage);
        expectedDiscount.setApplied(false);

        verify(discountRepository).save(expectedDiscount);
    }

    /**
     * Testing calculateDiscount
     **/
    @Test
    void whenCalculatingDiscountFor15MinutesLate_thenDiscountIsCreated() {
        // Given
        String rut = "12.345.678-9";
        Date date = Date.valueOf("2024-08-17");
        Time time = Time.valueOf("08:15:00");
        long minutes = 15; // Calcula para 15 minutos, caso de > 10 < 25, descuento de 1%

        // When
        discountService.calculateDiscount(rut, date, time, minutes);

        // Then
        verify(discountRepository).save(any());
        verify(absenceService, never()).createAbsence(any(), any(), anyLong());
    }

    @Test
    void whenMinutesIsBetween26And45_thenDiscountPercentageIsThreePercent() {
        // Given
        String rut = "12.345.678-9";
        Date date = Date.valueOf("2024-10-01");
        Time time = Time.valueOf("08:30:00");
        long minutes = 30; // Calcula para 30 minutos, caso de > 25 < 45 min, descuento de 3%

        // When
        discountService.calculateDiscount(rut, date, time, minutes);

        // Then
        // Verifica que createDiscount es llamado y calcular un descuento del 3%
        verify(discountRepository, times(1)).save(argThat(discount -> discount.getPercentage() == 0.03));
    }

    @Test
    void whenMinutesIsBetween46And70_thenDiscountPercentageIsSixPercent() {
        // Given
        String rut = "98.765.432-1";
        Date date = Date.valueOf("2024-10-01");
        Time time = Time.valueOf("09:00:00");
        long minutes = 50; // Calcula para 50 minutos, caso de > 46 < 70, descuento de 6%

        // When
        discountService.calculateDiscount(rut, date, time, minutes);

        // Then
        // Verifica que createDiscount es llamado y calcular un descuento del 6%
        verify(discountRepository, times(1)).save(argThat(discount -> discount.getPercentage() == 0.06));
    }

    @Test
    void whenCalculatingDiscountForMoreThan70Minutes_thenCreateAbsence() {
        // Given
        String rut = "12.345.678-9";
        Date date = Date.valueOf("2024-08-17");
        Time time = Time.valueOf("08:15:00");
        long minutes = 75; // Calcula para 75 minutos, caso de > 70, crea una inasistencia

        // When
        discountService.calculateDiscount(rut, date, time, minutes);

        // Then
        verify(absenceService).createAbsence(rut, date, minutes);
        verify(discountRepository, never()).save(any());
    }


    /**
     * Testing calculateTotalDiscount
     **/
    @Test
    void whenCalculatingTotalDiscount_thenReturnsCorrectValue() {
        // Given
        String rut = "12.345.678-9";
        int year = 2024;
        int month = 8;
        int baseSalary = 1000;

        DiscountEntity discount1 = new DiscountEntity();
        discount1.setPercentage(0.01);
        DiscountEntity discount2 = new DiscountEntity();
        discount2.setPercentage(0.03);

        when(discountRepository.filterByRutYearAndMonth(rut, year, month)).thenReturn(List.of(discount1, discount2));
        when(absenceService.calculateUnexcusedAbsence(rut, year, month, baseSalary)).thenReturn(0);

        // When
        int totalDiscount = discountService.calculateTotalDiscount(rut, year, month, baseSalary);

        // Then
        assertThat(totalDiscount).isEqualTo(40); // 10 + 30
    }

    /**
     * Testing getDiscountByRutAndDate
     **/
    @Test
    void whenDiscountExists_thenReturnsDiscount() {
        // Given
        String rut = "12.345.678-9";
        Date date = Date.valueOf("2024-08-17");
        DiscountEntity discount = new DiscountEntity();
        when(discountRepository.findByRutAndDate(rut, date)).thenReturn(discount);

        // When
        DiscountEntity foundDiscount = discountService.getDiscountByRutAndDate(rut, date);

        // Then
        assertThat(foundDiscount).isEqualTo(discount);
    }

    @Test
    void whenDiscountDoesNotExist_thenReturnsNull() {
        // Given
        String rut = "12.345.678-9";
        Date date = Date.valueOf("2024-08-17");
        when(discountRepository.findByRutAndDate(rut, date)).thenReturn(null);

        // When
        DiscountEntity foundDiscount = discountService.getDiscountByRutAndDate(rut, date);

        // Then
        assertThat(foundDiscount).isNull();
    }

    /**
     * Testing applyDiscount
     **/
    @Test
    void whenApplyingDiscount_thenDiscountIsUpdated() {
        // Given
        String rut = "12.345.678-9";
        Date date = Date.valueOf("2024-08-17");
        DiscountEntity discount = new DiscountEntity();
        discount.setApplied(false);
        when(discountRepository.findByRutAndDate(rut, date)).thenReturn(discount);

        // When
        discountService.applyDiscount(rut, date);

        // Then
        assertThat(discount.isApplied()).isTrue();
        verify(discountRepository).save(discount);
    }

    /**
     * Testing applyDiscounts
     **/
    @Test
    void whenApplyingDiscounts_thenAllDiscountsAreApplied() {
        // Given
        int year = 2020;
        int month = 5;

        // Primer descuento
        DiscountEntity discount1 = new DiscountEntity();
        discount1.setApplied(false);
        discount1.setRut("12.345.678-9");
        discount1.setDate(Date.valueOf("2020-05-10"));
        discount1.setTime(Time.valueOf("08:20:00"));
        discount1.setMinutes(20L);

        // Segundo descuento
        DiscountEntity discount2 = new DiscountEntity();
        discount2.setApplied(false);
        discount2.setRut("98.765.432-1");
        discount2.setDate(Date.valueOf("2020-05-10"));
        discount2.setTime(Time.valueOf("08:30:00"));
        discount2.setMinutes(30L);

        // Mock de repositorio retornando una lista con los dos descuentos
        when(discountRepository.filterByYearAndMonth(year, month)).thenReturn(List.of(discount1, discount2));

        // Mock de getDiscountByRutAndDate para ambos descuentos
        when(discountRepository.findByRutAndDate(discount1.getRut(), discount1.getDate())).thenReturn(discount1);
        when(discountRepository.findByRutAndDate(discount2.getRut(), discount2.getDate())).thenReturn(discount2);

        // When
        discountService.applyDiscounts(year, month);

        // Then
        verify(discountRepository, times(2)).save(any(DiscountEntity.class));
    }

}
