package com.example.mueblesStgoBackend.services;

import com.example.mueblesStgoBackend.entities.ExtraHoursAuthorizationEntity;
import com.example.mueblesStgoBackend.entities.ExtraHoursEntity;
import com.example.mueblesStgoBackend.repositories.ExtraHoursAuthorizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExtraHoursAuthorizationServiceTest {

    @Mock
    ExtraHoursAuthorizationRepository extraHoursAuthorizationRepository;

    @Mock
    ExtraHoursService extraHoursService;

    @InjectMocks
    ExtraHoursAuthorizationService extraHoursAuthorizationService = new ExtraHoursAuthorizationService();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Testing findAuthorization
     */
    @Test
    void whenFindingAuthorization_thenReturnsAuthorization() {
        // Given
        String rut = "12.345.678-9";
        Date date = Date.valueOf("2024-10-01");
        ExtraHoursAuthorizationEntity authorization = new ExtraHoursAuthorizationEntity();
        authorization.setRut(rut);
        authorization.setDate(date);

        when(extraHoursAuthorizationRepository.findByRutAndDate(rut, date)).thenReturn(authorization);

        // When
        ExtraHoursAuthorizationEntity result = extraHoursAuthorizationService.findAuthorization(rut, date);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRut()).isEqualTo(rut);
        assertThat(result.getDate()).isEqualTo(date);
        verify(extraHoursAuthorizationRepository, times(1)).findByRutAndDate(rut, date);
    }

    /**
     * Testing getAllByYearAndMonth
     */
    @Test
    void whenGettingAllByYearAndMonth_thenReturnsListOfAuthorizations() {
        // Given
        int year = 2024;
        int month = 10;
        List<ExtraHoursAuthorizationEntity> authorizations = new ArrayList<>();
        authorizations.add(new ExtraHoursAuthorizationEntity());
        authorizations.add(new ExtraHoursAuthorizationEntity());

        when(extraHoursAuthorizationRepository.findAllByYearAndMonth(year, month)).thenReturn(authorizations);

        // When
        List<ExtraHoursAuthorizationEntity> result = extraHoursAuthorizationService.getAllByYearAndMonth(year, month);

        // Then
        assertThat(result).hasSize(2);
        verify(extraHoursAuthorizationRepository, times(1)).findAllByYearAndMonth(year, month);
    }

    /**
     * Testing addAuthorization
     */
    @Test
    void whenAddingAuthorization_AlreadyExists_ReturnsConflict() {
        // Given
        ExtraHoursAuthorizationEntity auth = new ExtraHoursAuthorizationEntity();
        auth.setRut("12.345.678-9");
        auth.setDate(Date.valueOf("2024-10-01"));

        when(extraHoursAuthorizationRepository.findByRutAndDate(auth.getRut(), auth.getDate())).thenReturn(auth);

        // When
        ResponseEntity<String> response = extraHoursAuthorizationService.addAuthorization(auth);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isEqualTo("Conflicto: Una autorización para esta persona y fecha ya ha sido ingresada");
        verify(extraHoursAuthorizationRepository, times(0)).save(auth);
    }

    @Test
    void whenAddingAuthorization_DoesNotExist_ReturnsSuccess() {
        // Given
        ExtraHoursAuthorizationEntity auth = new ExtraHoursAuthorizationEntity();
        auth.setRut("12.345.678-9");
        auth.setDate(Date.valueOf("2024-10-01"));

        when(extraHoursAuthorizationRepository.findByRutAndDate(auth.getRut(), auth.getDate())).thenReturn(null);

        // When
        ResponseEntity<String> response = extraHoursAuthorizationService.addAuthorization(auth);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Autorización ingresada correctamente.");
        verify(extraHoursAuthorizationRepository, times(1)).save(auth);
    }

    @Test
    void whenAuthorizingExtraHoursByYearAndMonth_thenAuthorizesExtraHours() {
        // Given
        int year = 2024;
        int month = 10;

        ExtraHoursEntity extraHour = new ExtraHoursEntity();
        extraHour.setId(1L);
        extraHour.setRut("12.345.678-9");
        extraHour.setDate(Date.valueOf("2024-10-01"));
        extraHour.setAuthorized(false);

        ExtraHoursAuthorizationEntity authorization = new ExtraHoursAuthorizationEntity();
        authorization.setId(1L);
        authorization.setRut(extraHour.getRut());
        authorization.setDate(extraHour.getDate());

        // Mocking
        when(extraHoursService.getAllByYearAndMonth(year, month)).thenReturn(List.of(extraHour));
        when(extraHoursAuthorizationRepository.findByRutAndDate(extraHour.getRut(), extraHour.getDate())).thenReturn(authorization);
        when(extraHoursService.getByRutAndYearAndMonth(extraHour.getId(), extraHour.getRut(), year, month)).thenReturn(extraHour);

        // When
        extraHoursAuthorizationService.authorizeExtraHoursByYearAndMonth(year, month);

        // Then
        verify(extraHoursService, times(1)).getAllByYearAndMonth(year, month);
        verify(extraHoursAuthorizationRepository, times(1)).findByRutAndDate(extraHour.getRut(), extraHour.getDate());
        verify(extraHoursService, times(1)).updateAuthorization(extraHour, true);
    }



}
