package com.example.mueblesStgoBackend.services;

import com.example.mueblesStgoBackend.entities.AbsenceEntity;
import com.example.mueblesStgoBackend.repositories.AbsenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AbsenceServiceTest {

    @Mock
    private AbsenceRepository absenceRepository;

    @InjectMocks
    AbsenceService absenceService = new AbsenceService();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Testing createAbsence
     */
    @Test
    void whenCreatingAbsence_thenSavesAbsence() {
        // Given
        String rut = "12.345.678-9";
        Date date = Date.valueOf("2024-10-01");
        long minutes = 30;

        // When
        absenceService.createAbsence(rut, date, minutes);

        // Then
        AbsenceEntity absence = new AbsenceEntity();
        absence.setRut(rut);
        absence.setDate(date);
        absence.setMinutes(minutes);
        absence.setExcused(false);
        verify(absenceRepository, times(1)).save(absence);
    }

    /**
     * Testing findAbsenceByRutAndDate
     */
    @Test
    void whenFindingAbsenceByRutAndDate_thenReturnsAbsence() {
        // Given
        String rut = "12.345.678-9";
        Date date = Date.valueOf("2024-10-01");
        AbsenceEntity absence = new AbsenceEntity();
        absence.setRut(rut);
        absence.setDate(date);
        absence.setMinutes(30);
        absence.setExcused(false);

        when(absenceRepository.findAbsence(rut, date)).thenReturn(absence);

        // When
        AbsenceEntity result = absenceService.findAbsenceByRutAndDate(rut, date);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRut()).isEqualTo(rut);
        assertThat(result.getDate()).isEqualTo(date);
        verify(absenceRepository, times(1)).findAbsence(rut, date);
    }

    /**
     * Testing findAllUnexcusedByYearAndMonth
     */
    @Test
    void whenFindingAllUnexcusedByYearAndMonth_thenReturnsListOfUnexcusedAbsences() {
        // Given
        int year = 2024;
        int month = 10;
        List<AbsenceEntity> unexcusedAbsences = new ArrayList<>();
        unexcusedAbsences.add(new AbsenceEntity());
        unexcusedAbsences.add(new AbsenceEntity());

        when(absenceRepository.filterAllUnexcusedByYearAndMonth(year, month)).thenReturn(unexcusedAbsences);

        // When
        List<AbsenceEntity> result = absenceService.findAllUnexcusedByYearAndMonth(year, month);

        // Then
        assertThat(result).hasSize(2);
        verify(absenceRepository, times(1)).filterAllUnexcusedByYearAndMonth(year, month);
    }

    /**
     * Testing calculateUnexcusedAbsence
     */
    @Test
    void whenCalculatingUnexcusedAbsence_thenReturnsTotalDiscount() {
        // Given
        String rut = "12.345.678-9";
        int year = 2024;
        int month = 10;
        int baseSalary = 1000;

        List<AbsenceEntity> absences = new ArrayList<>();
        AbsenceEntity absence1 = new AbsenceEntity();
        absences.add(absence1);
        AbsenceEntity absence2 = new AbsenceEntity();
        absences.add(absence2);

        when(absenceRepository.filterUnexcusedByRutYearAndMonth(rut, year, month)).thenReturn(absences);

        // When
        int result = absenceService.calculateUnexcusedAbsence(rut, year, month, baseSalary);

        // Then
        // Calcula descuento para dos ausencias
        int expectedDiscount = (int) Math.floor(2 * (baseSalary * 0.15));
        assertThat(result).isEqualTo(expectedDiscount);
        verify(absenceRepository, times(1)).filterUnexcusedByRutYearAndMonth(rut, year, month);
    }

}
