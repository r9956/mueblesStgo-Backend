package com.example.mueblesStgoBackend.services;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class ClockDataServiceTest {

    ClockDataService clockData = new ClockDataService();

    /**
     * Tests for fileTypeValidation
     * **/

    @Test
    void whenFileTypeValidation_thenTrue() {
        // Given
        MultipartFile mockFile = new MockMultipartFile("file", "DATA.txt", null, (byte[]) null);

        // When
        boolean validFileType = clockData.fileTypeValidation(mockFile);

        // Then
        assertThat(validFileType).isTrue();
    }

    @Test
    void whenWrongFileType_ThenFalse() {
        // Given
        MultipartFile mockFile = new MockMultipartFile("file", "DATA.pdf", null, (byte[]) null);

        // When
        boolean validFileType = clockData.fileTypeValidation(mockFile);

        // Then
        assertThat(validFileType).isFalse();
    }

    @Test
    void whenFileIsNull_thenFalse() {
        // When
        boolean validFileType = clockData.fileTypeValidation(null);

        // Then
        assertThat(validFileType).isFalse();
    }

    @Test
    void whenFileNameIsNull_thenFalse() {
        // Given
        MultipartFile mockFile = new MockMultipartFile("file", null, null, (byte[]) null);

        // When
        boolean validFileType = clockData.fileTypeValidation(mockFile);

        // Then
        assertThat(validFileType).isFalse();
    }

    /**
     * Tests for fileDataFormatValidation
     **/

    @Test
    void whenFileDataFormatIsCorrect_thenTrue() {
        // Given
        String[] stringArray = new String[3];

        // When
        boolean validFileDataFormat = clockData.fileDataFormatValidation(stringArray);

        // Then
        assertThat(validFileDataFormat).isTrue();
    }

    @Test
    void whenFileDataFormatIsNotCorrect_thenFalse() {
        // Given
        String[] stringArray = new String[2];

        // When
        boolean validFileDataFormat = clockData.fileDataFormatValidation(stringArray);

        // Then
        assertThat(validFileDataFormat).isFalse();
    }

    @Test
    void whenFileDataFormatIsNull_thenFalse() {
        // When
        boolean validFileDataFormat = clockData.fileDataFormatValidation(null);

        // Then
        assertThat(validFileDataFormat).isFalse();
    }

    @Test
    void whenFileDataFormatIsEmpty_thenFalse() {
        // Given
        String[] stringArray = new String[0];

        // When
        boolean validFileDataFormat = clockData.fileDataFormatValidation(stringArray);

        // Then
        assertThat(validFileDataFormat).isFalse();
    }

    /**
     * Tests for dateFormatValidation
     **/

    @Test
    void whenDateFormatIsCorrect_thenTrue() {
        // Given
        String date = "2019/05/08"; // "DATA.txt" file contains dates formatted with '/' instead of '-'

        // When
        boolean validDate = clockData.dateFormatValidation(date);

        // Then
        assertThat(validDate).isTrue();
    }

    @Test
    void whenDateFormatIsNotCorrect_thenFalse() {
        // Given
        String date = "2019-05-08"; // "DATA.txt" file contains dates formatted with '/' instead of '-'

        // When
        boolean isDateCorrect = clockData.dateFormatValidation(date);

        // Then
        assertThat(isDateCorrect).isFalse();
    }

    @Test
    void whenDateFormatIsReversed_thenFalse() {
        // Given
        String date = "05-08-2019";

        // When
        boolean isDateCorrect = clockData.dateFormatValidation(date);

        // Then
        assertThat(isDateCorrect).isFalse();
    }

    @Test
    void whenDateFormatContainsLetters_thenFalse() {
        // Invalid format including letters
        assertThat(clockData.dateFormatValidation("2020/MA/05")).isFalse();
        assertThat(clockData.dateFormatValidation("2020/05/FR")).isFalse();
    }

    @Test
    void whenInvalidDayRange_thenFalse() {
        // Day out of range
        assertThat(clockData.dateFormatValidation("2020/05/32")).isFalse();
        assertThat(clockData.dateFormatValidation("2020/05/00")).isFalse();
    }

    @Test
    void whenInvalidMonthRange_thenFalse() {
        // Month out of range
        assertThat(clockData.dateFormatValidation("2020/00/15")).isFalse();
        assertThat(clockData.dateFormatValidation("2020/13/15")).isFalse();
    }

    @Test
    void whenShortYearFormat_thenFalse() {
        // Short year like 20 instead of 2020
        assertThat(clockData.dateFormatValidation("20/05/15")).isFalse();
    }

    /**
     * Tests for timeFormatValidation
     **/

    @Test
    void whenTimeFormatIsCorrect_thenTrue() {
        assertThat(clockData.timeFormatValidation("08:07")).isTrue();
        assertThat(clockData.timeFormatValidation("23:59")).isTrue();
    }

    @Test
    void whenIncorrectFormat_thenFalse() {
        assertThat(clockData.timeFormatValidation("8:07")).isFalse(); // Incorrect hour format
        assertThat(clockData.timeFormatValidation("08:7")).isFalse(); // Incorrect minute format
        assertThat(clockData.timeFormatValidation("08-07")).isFalse(); // Incorrect separator
        assertThat(clockData.timeFormatValidation("08:07:00")).isFalse(); // Seconds included
    }

    @Test
    void whenTimeIsOutOfRange_thenFalse() {
        assertThat(clockData.timeFormatValidation("24:00")).isFalse();
        assertThat(clockData.timeFormatValidation("90:00")).isFalse();
        assertThat(clockData.timeFormatValidation("12:60")).isFalse();
        assertThat(clockData.timeFormatValidation("12:99")).isFalse();
    }

    @Test
    void whenNullOrEmpty_thenFalse() {
        assertThat(clockData.timeFormatValidation(null)).isFalse();
        assertThat(clockData.timeFormatValidation("")).isFalse();
    }

    /**
     * Tests for doesEntryExist
     **/

//    @Test
//    void whenEntryAlreadyExistsInDatabase_thenTrue() {
//        // Given
//        Date date = Date.valueOf("2023-09-15");
//        Time time = Time.valueOf("08:07:00");
//        String rut = "12345678-9";
//
//        ClockDataEntity mockClockData = new ClockDataEntity(1L, date, time, rut);
//        when(clockDataRepository.findEntry(date, time, rut)).thenReturn(mockClockData);
//
//        // When
//        boolean doesEntryExist = clockData.doesEntryExist(date, time, rut);
//
//        // Then
//        assertThat(doesEntryExist).isTrue();
//    }

}
