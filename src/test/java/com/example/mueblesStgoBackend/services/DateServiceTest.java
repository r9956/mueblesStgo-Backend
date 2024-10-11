package com.example.mueblesStgoBackend.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class DateServiceTest {

    private DateService dateService;

    @BeforeEach
    void setUp() {
        dateService = new DateService();
    }

    /**
     * Tests for calculateDaysBetween
     **/

    @Test
    void whenStartDateIsBeforeEndDate_thenReturnsPositiveDays() {
        // Given
        Date fromDate = Date.valueOf("2023-01-01");
        Date toDate = Date.valueOf("2023-01-10");

        // When
        long daysBetween = dateService.calculateDaysBetween(fromDate, toDate);

        // Then
        assertThat(daysBetween).isEqualTo(9);
    }

    @Test
    void whenStartDateIsSameAsEndDate_thenReturnsZeroDays() {
        // Given
        Date fromDate = Date.valueOf("2023-01-10");
        Date toDate = Date.valueOf("2023-01-10");

        // When
        long daysBetween = dateService.calculateDaysBetween(fromDate, toDate);

        // Then
        assertThat(daysBetween).isEqualTo(0);
    }

    @Test
    void whenStartDateIsAfterEndDate_thenReturnsNegativeDays() {
        // Given
        Date fromDate = Date.valueOf("2023-01-10");
        Date toDate = Date.valueOf("2023-01-01");

        // When
        long daysBetween = dateService.calculateDaysBetween(fromDate, toDate);

        // Then
        assertThat(daysBetween).isEqualTo(-9);
    }

    /**
     * Tests for isDateRangeValid
     **/

    @Test
    void whenFromDateIsBeforeToDate_thenIsDateRangeValidReturnsTrue() {
        // Given
        Date fromDate = Date.valueOf("2023-01-01");
        Date toDate = Date.valueOf("2023-01-10");

        // When
        boolean isValid = dateService.isDateRangeValid(fromDate, toDate);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void whenFromDateIsSameAsToDate_thenIsDateRangeValidReturnsTrue() {
        // Given
        Date fromDate = Date.valueOf("2023-01-10");
        Date toDate = Date.valueOf("2023-01-10");

        // When
        boolean isValid = dateService.isDateRangeValid(fromDate, toDate);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void whenFromDateIsAfterToDate_thenIsDateRangeValidReturnsFalse() {
        // Given
        Date fromDate = Date.valueOf("2023-01-10");
        Date toDate = Date.valueOf("2023-01-01");

        // When
        boolean isValid = dateService.isDateRangeValid(fromDate, toDate);

        // Then
        assertThat(isValid).isFalse();
    }
}
