package com.example.mueblesStgoBackend.services;

import com.example.mueblesStgoBackend.entities.AbsenceEntity;
import com.example.mueblesStgoBackend.entities.AbsenceExcuseEntity;
import com.example.mueblesStgoBackend.repositories.AbsenceExcuseRepository;
import com.example.mueblesStgoBackend.repositories.AbsenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AbsenceExcuseServiceTest {

    @Mock
    private EmployeeService employeeService;

    @Mock
    private DateService dateService;

    @Mock
    private AbsenceService absenceService;

    @Mock
    private AbsenceRepository absenceRepository;

    @Mock
    private AbsenceExcuseRepository absenceExcuseRepository;

    @InjectMocks
    private AbsenceExcuseService absenceExcuseService;

    @Test
    void whenAddingExcuseWithExistingExcuse_thenConflictResponse() {
        // Given
        String rut = "12.345.678-9";
        Date fromDate = Date.valueOf("2024-10-01");
        Date toDate = Date.valueOf("2024-10-05");
        MultipartFile file = mock(MultipartFile.class);

        when(absenceExcuseRepository.findAbsenceExcuse(rut, fromDate, toDate)).thenReturn(new AbsenceExcuseEntity());

        // When
        ResponseEntity<String> response = absenceExcuseService.addExcuse(rut, fromDate, toDate, file);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Error: Una justificación para este empleado y fecha ya ha sido ingresada previamente.", response.getBody());
    }

    @Test
    void whenAddingExcuseWithInvalidRutFormat_thenConflictResponse() {
        // Given
        String rut = "10258965-4";
        Date fromDate = Date.valueOf("2024-10-01");
        Date toDate = Date.valueOf("2024-10-05");
        MultipartFile file = mock(MultipartFile.class);

        when(employeeService.rutFormatValidation(rut)).thenReturn(false);

        // When
        ResponseEntity<String> response = absenceExcuseService.addExcuse(rut, fromDate, toDate, file);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Error: El rut 10258965-4 no es válido.", response.getBody());
    }

    @Test
    void whenAddingExcuseWithNonExistingEmployee_thenConflictResponse() {
        // Given
        String rut = "12.345.678-9";
        Date fromDate = Date.valueOf("2024-10-01");
        Date toDate = Date.valueOf("2024-10-05");
        MultipartFile file = mock(MultipartFile.class);

        when(employeeService.rutFormatValidation(rut)).thenReturn(true);
        when(employeeService.doesRutExists(rut)).thenReturn(false);

        // When
        ResponseEntity<String> response = absenceExcuseService.addExcuse(rut, fromDate, toDate, file);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Error: No hay un empleado registrado con el RUT: " + rut + ".", response.getBody());
    }

    @Test
    void whenAddingExcuseWithInvalidDateRange_thenConflictResponse() {
        // Given
        String rut = "12.345.678-9";
        Date fromDate = Date.valueOf("2024-10-06");
        Date toDate = Date.valueOf("2024-10-01");
        MultipartFile file = mock(MultipartFile.class);

        when(employeeService.rutFormatValidation(rut)).thenReturn(true);
        when(employeeService.doesRutExists(rut)).thenReturn(true);
        when(dateService.isDateRangeValid(fromDate, toDate)).thenReturn(false);

        // When
        ResponseEntity<String> response = absenceExcuseService.addExcuse(rut, fromDate, toDate, file);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Error: El rango de fechas no es válido.", response.getBody());
    }

    @Test
    void whenAddingValidExcuse_thenSuccessResponse() {
        // Given
        String rut = "12.345.678-9";
        Date fromDate = Date.valueOf("2024-10-01");
        Date toDate = Date.valueOf("2024-10-05");
        MultipartFile file = mock(MultipartFile.class);

        when(employeeService.rutFormatValidation(rut)).thenReturn(true);
        when(employeeService.doesRutExists(rut)).thenReturn(true);
        when(dateService.isDateRangeValid(fromDate, toDate)).thenReturn(true);
        when(absenceExcuseRepository.findAbsenceExcuse(rut, fromDate, toDate)).thenReturn(null);

        // When
        ResponseEntity<String> response = absenceExcuseService.addExcuse(rut, fromDate, toDate, file);

        // Then
        verify(absenceExcuseRepository, times(1)).save(any(AbsenceExcuseEntity.class));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Justificación de ausencia ingresada correctamente.", response.getBody());
    }

    @Test
    void whenCheckingIfAbsenceExcuseExists_thenReturnsTrue() {
        // Given
        String rut = "12.345.678-9";
        Date fromDate = Date.valueOf("2024-10-01");
        Date toDate = Date.valueOf("2024-10-05");

        when(absenceExcuseRepository.findAbsenceExcuse(rut, fromDate, toDate)).thenReturn(new AbsenceExcuseEntity());

        // When
        boolean exists = absenceExcuseService.doesAbsenceExcuseExists(rut, fromDate, toDate);

        // Then
        assertTrue(exists);
    }

    @Test
    void whenAbsenceExcuseDoesNotExist_thenReturnsFalse() {
        // Given
        String rut = "12.345.678-9";
        Date fromDate = Date.valueOf("2024-10-01");
        Date toDate = Date.valueOf("2024-10-05");

        when(absenceExcuseRepository.findAbsenceExcuse(rut, fromDate, toDate)).thenReturn(null);

        // When
        boolean exists = absenceExcuseService.doesAbsenceExcuseExists(rut, fromDate, toDate);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void whenApplyingExcuses_thenAbsencesAreExcused() {
        // Given
        int year = 2024;
        int month = 10;

        AbsenceEntity absence = new AbsenceEntity();
        absence.setRut("12.345.678-9");
        absence.setDate(Date.valueOf("2024-10-01"));
        absence.setExcused(false);

        AbsenceExcuseEntity excuse = new AbsenceExcuseEntity();
        excuse.setRut("12.345.678-9");
        excuse.setFromDate(Date.valueOf("2024-10-01"));
        excuse.setToDate(Date.valueOf("2024-10-05"));

        when(absenceService.findAllUnexcusedByYearAndMonth(year, month)).thenReturn(List.of(absence));
        when(absenceExcuseRepository.findAllByRutAndYearAndMonth(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(excuse));

        // When
        absenceExcuseService.applyExcuses(year, month);

        // Then
        verify(absenceRepository, times(1)).save(absence);
        assertThat(absence.isExcused()).isTrue();
    }

    @Test
    void whenExcusingAbsence_thenAbsenceIsMarkedAsExcused() {
        // Given
        AbsenceEntity absence = new AbsenceEntity();
        absence.setRut("12.345.678-9");
        absence.setExcused(false);

        // When
        absenceExcuseService.excuseAbsence(absence);

        // Then
        verify(absenceRepository, times(1)).save(absence);
        assertThat(absence.isExcused()).isTrue();
    }

    @Test
    void whenExcusingAlreadyExcusedAbsence_thenNotSavedAgain() {
        // Given
        AbsenceEntity absence = new AbsenceEntity();
        absence.setRut("12.345.678-9");
        absence.setExcused(true);

        // When
        absenceExcuseService.excuseAbsence(absence);

        // Then
        verify(absenceRepository, never()).save(absence);
    }

    @Test
    void whenValidatingAbsenceDates_thenReturnsTrueForValidRange() {
        // Given
        Date fromDate = Date.valueOf("2024-10-01");
        Date toDate = Date.valueOf("2024-10-05");

        when(dateService.isDateRangeValid(fromDate, toDate)).thenReturn(true);

        // When
        boolean isValid = absenceExcuseService.validateAbsenceDates(fromDate, toDate);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void whenValidatingAbsenceDates_thenReturnsFalseForInvalidRange() {
        // Given
        Date fromDate = Date.valueOf("2024-10-05");
        Date toDate = Date.valueOf("2024-10-01");

        // When
        boolean isValid = absenceExcuseService.validateAbsenceDates(fromDate, toDate);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void whenAbsenceDateIsWithinRange_thenReturnsTrue() {
        // Given
        Date absenceDate = Date.valueOf("2024-10-03");
        Date fromDate = Date.valueOf("2024-10-01");
        Date toDate = Date.valueOf("2024-10-05");

        // When
        boolean isWithinRange = absenceExcuseService.isDateWithinRange(absenceDate, fromDate, toDate);

        // Then
        assertTrue(isWithinRange);
    }

}
