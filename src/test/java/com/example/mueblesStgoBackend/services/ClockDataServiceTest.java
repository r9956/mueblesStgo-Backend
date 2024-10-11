package com.example.mueblesStgoBackend.services;

import com.example.mueblesStgoBackend.entities.ClockDataEntity;
import com.example.mueblesStgoBackend.repositories.ClockDataRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@ExtendWith(MockitoExtension.class)
public class ClockDataServiceTest {

    @Mock
    private ClockDataRepository clockDataRepository;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private DiscountService discountService;

    @Mock
    private ExtraHoursService extraHoursService;

    @InjectMocks
    ClockDataService clockData = new ClockDataService();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

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

    @Test
    void whenEntryAlreadyExistsInDatabase_thenTrue() {
        // Given
        Date date = Date.valueOf("2023-09-15");
        Time time = Time.valueOf("08:07:00");
        String rut = "12345678-9";

        ClockDataEntity mockClockData = new ClockDataEntity(1L, date, time, rut, false);
        when(clockDataRepository.findEntry(date, time, rut)).thenReturn(mockClockData);

        // When
        boolean doesEntryExist = clockData.doesEntryExist(date, time, rut);

        // Then
        assertThat(doesEntryExist).isTrue();
    }

    /**
     * Tests for fileReader
     **/

    @Test
    void fileReader_validFile() {
        // Given a Mock File
        String fileContent =    "2022/08/17;08:00;11.234.123-6\n" +
                                "2022/08/17;07:58;12.457.562-3";

        MockMultipartFile mockFile = new MockMultipartFile("file", "DATA.txt", "text/plain", fileContent.getBytes());

        when(employeeService.rutFormatValidation("11.234.123-6")).thenReturn(true);
        when(employeeService.rutFormatValidation("12.457.562-3")).thenReturn(true);

        // When
        ResponseEntity<String> response = clockData.fileReader(mockFile);

        // Then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void fileReader_invalidFileType() {
        // Given
        String fileContent = "2022/08/17;08:00;11.234.123-6\n";
        MockMultipartFile mockFile = new MockMultipartFile("file", "DATA.pdf", "application/pdf", fileContent.getBytes());

        // When
        ResponseEntity<String> response = clockData.fileReader(mockFile);

        // Then
        Assertions.assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
        Assertions.assertEquals("Only .txt files are supported.", response.getBody());
    }

    @Test
    void fileReader_validfileWithInvalidDateFormat() {
        // Given
        String fileContent = "2022/99/17;08:00;12345678\n" + // Mes no válido
                "2022/08/17;08:00;10.234.123-6";

        MockMultipartFile mockFile = new MockMultipartFile("file", "DATA.txt", "text/plain", fileContent.getBytes());

        when(employeeService.rutFormatValidation("10.234.123-6")).thenReturn(true); // RUT válido para la segunda línea

        // When
        ResponseEntity<String> response = clockData.fileReader(mockFile);

        // Then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        // Primera línea es saltada debido a error en el mes de la fecha.
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("1 entradas sin añadir debido a errores de formato."));
        // Segunda línea es añadida correctamente.
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("1 entradas añadidas correctamente."));
    }

    @Test
    void fileReader_validFileWithInvalidRutFormat() {
        // Given a Mock File with invalid RUT format
        String fileContent =    "2022/08/17;08:00;10.234.123-6\n" +
                "2022/08/17;07:58;102341236";

        MockMultipartFile mockFile = new MockMultipartFile("file", "DATA.txt", "text/plain", fileContent.getBytes());

        when(employeeService.rutFormatValidation("10.234.123-6")).thenReturn(true); // RUT válido
        when(employeeService.rutFormatValidation("102341236")).thenReturn(false);  // RUT inválido

        // When
        ResponseEntity<String> response = clockData.fileReader(mockFile);

        // Then -> Valida el archivo pero se salta la línea con el rut inválido
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode()); // Archivo válido
        // 1 entrada correctamente añadida
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("1 entradas añadidas correctamente."));
        // 1 entrada sin añadir por rut inválido
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("1 entradas sin añadir debido a errores de formato."));
    }

    @Test
    void fileReader_fileWithInvalidTimeFormat() {
        // Given
        String fileContent =    "2022/08/17;25:00;10.234.123-6\n" + // Hora no válida por estar fuera de rango
                                "2022/08/17;08:00:00;22.145.023-2\n" +  // Hora no válida por formato incompatible al incluir segundos
                                "2022/08/17;08:15;18.023.941-3"; // Formato correcto

        MockMultipartFile mockFile = new MockMultipartFile("file", "DATA.txt", "text/plain", fileContent.getBytes());
        when(employeeService.rutFormatValidation("18.023.941-3")).thenReturn(true); // RUT válido

        // When
        ResponseEntity<String> response = clockData.fileReader(mockFile);

        // Then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        // 1 entrada correctamente añadida
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("1 entradas añadidas correctamente."));
        // 2 entradas evitadas por errores de formato de hora
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("2 entradas sin añadir debido a errores de formato."));
    }

    @Test
    void fileReader_fileWithInvalidDataFormat() {
        // Given
        String fileContent = "0;0"; // Formato de datos no válido

        MockMultipartFile mockFile = new MockMultipartFile("file", "DATA.txt", "text/plain", fileContent.getBytes());

        // When
        ResponseEntity<String> response = clockData.fileReader(mockFile);

        // Then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("El archivo no contiene información válida."));
    }

    @Test
    void fileReader_fileWithDuplicateEntryInDatabase() {
        // Given
        String fileContent = "2022/08/17;08:00;11.234.123-6";

        MockMultipartFile mockFile = new MockMultipartFile("file", "DATA.txt", "text/plain", fileContent.getBytes());
        when(employeeService.rutFormatValidation("11.234.123-6")).thenReturn(true);

        // Mocking de fecha y hora
        LocalDate localDate = LocalDate.parse("2022/08/17", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        LocalTime localTime = LocalTime.parse("08:00");

        // Conversión a fecha y hora de tipo SQL
        Date date = Date.valueOf(localDate);
        Time time = Time.valueOf(localTime);

        // Mocking de una entrada previa en la base de datos exactamente igual a la información del archivo
        when(clockDataRepository.findEntry(eq(date), eq(time), eq("11.234.123-6"))).thenReturn(new ClockDataEntity()); // Simulate existing entry

        // When
        ResponseEntity<String> response = clockData.fileReader(mockFile);

        // Then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("0 entradas fueron añadidas."));
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("1 entradas ya existían en la base de datos y no fueron añadidas para evitar duplicación."));
    }

    @Test
    void fileReader_ioException() {
        // Given
        String fileContent = "2022/08/17;08:00;11.234.123-6\n";
        MockMultipartFile mockFile = new MockMultipartFile("file", "DATA.txt", "text/plain", fileContent.getBytes()) {
            @Override
            public InputStream getInputStream() throws IOException {
                throw new IOException("Simulated IOException");
            }
        };

        // When
        ResponseEntity<String> response = clockData.fileReader(mockFile);

        // Then
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertEquals("Error reading the file.", response.getBody());
    }

    /**
     * Tests for formatClockDataMessage
     */

    // 1. Caso exitoso
    @Test
    void whenAllEntriesAddedwithNoErrors_thenSuccessMessage() {
        // Given
        int entriesAdded = 10;
        int entriesSkipped = 0;
        int duplicatesAvoided = 0;
        String formattedDuration = "1.333";

        // When
        String result = clockData.formatClockDataMessage(entriesAdded, entriesSkipped, duplicatesAvoided, formattedDuration);

        // Then
        String message = """
                    RESULTADO: 10 entradas añadidas.
                    
                    Tiempo de procesamiento: 1.333 segundos""";
        Assertions.assertEquals(message, result);
    }

    // 2. Error: Archivo vacío o con errores
    @Test
    void whenNoEntriesAndNoSkipsAndNoDuplicates_thenEmptyFileErrorMessage() {
        // Given
        int entriesAdded = 0;
        int entriesSkipped = 0;
        int duplicatesAvoided = 0;
        String formattedDuration = "0.002";

        // When
        String result = clockData.formatClockDataMessage(entriesAdded, entriesSkipped, duplicatesAvoided, formattedDuration);

        // Then
        String message = """
                    ERROR: El archivo no contiene información válida.
                    
                    Tiempo de procesamiento: 0.002 segundos""";
        Assertions.assertEquals(message, result);
    }

    // 3. Entradas evitadas por errores de formato
    @Test
    void whenEntriesSkippedAndNoDuplicates_thenFormatErrorMessage() {
        // Given
        int entriesAdded = 0;
        int entriesSkipped = 3;
        int duplicatesAvoided = 0;
        String formattedDuration = "0.456";

        // When
        String result = clockData.formatClockDataMessage(entriesAdded, entriesSkipped, duplicatesAvoided, formattedDuration);

        // Then
        String message = """
                    ERROR: El archivo no contiene información válida.
                    
                    Tiempo de procesamiento: 0.456 segundos""";
        Assertions.assertEquals(message, result);
    }

    // 4. Entradas creadas y entradas evitadas
    @Test
    void whenEntriesAddedAndSkipped_thenAddedWithFormatErrorWarning() {
        // Given
        int entriesAdded = 2;
        int entriesSkipped = 2;
        int duplicatesAvoided = 0;
        String formattedDuration = "0.017";

        // When
        String result = clockData.formatClockDataMessage(entriesAdded, entriesSkipped, duplicatesAvoided, formattedDuration);

        // Then
        String message = """
                    ADVERTENCIA: El archivo fue procesado por el sistema pero presentó errores.
                    
                    RESULTADOS:
                    2 entradas añadidas correctamente.
                    2 entradas sin añadir debido a errores de formato.
                    
                    Tiempo de procesamiento: 0.017 segundos""";
        Assertions.assertEquals(message, result);
    }

    // 5. Entradas dupplicadas
    @Test
    void whenOnlyDuplicatesAvoided_thenDuplicatesWarning() {
        // Given
        int entriesAdded = 0;
        int entriesSkipped = 0;
        int duplicatesAvoided = 5;
        String formattedDuration = "1.678";

        // When
        String result = clockData.formatClockDataMessage(entriesAdded, entriesSkipped, duplicatesAvoided, formattedDuration);

        // Then
        String message = """
                    ADVERTENCIA: La información contenida en el archivo ya existe en el sistema.
                    
                    0 entradas fueron añadidas.
                    5 entradas ya existían en la base de datos y no fueron añadidas para evitar duplicación.
                    
                    Tiempo de procesamiento: 1.678 segundos""";
        Assertions.assertEquals(message, result);
    }

    // 6. Entradas evitadas y entradas duplicadas
    @Test
    void whenSkippedAndDuplicatesAvoided_thenFormatAndDuplicatesWarning() {
        // Given
        int entriesAdded = 0;
        int entriesSkipped = 3;
        int duplicatesAvoided = 2;
        String formattedDuration = "0.013";

        // When
        String result = clockData.formatClockDataMessage(entriesAdded, entriesSkipped, duplicatesAvoided, formattedDuration);

        // Then
        String message = """
                    ADVERTENCIA: El archivo fue procesado por el sistema pero presentó errores.
                    
                    RESULTADOS:
                    3 entradas sin añadir debido a errores de formato.
                    2 entradas ya existían en la base de datos y no fueron añadidas para evitar duplicación.
                    
                    Tiempo de procesamiento: 0.013 segundos""";
        Assertions.assertEquals(message, result);
    }

    // 7. Entradas creadas y entradas duplicadas
    @Test
    void whenEntriesAddedAndDuplicatesAvoided_thenAddedAndDuplicatesWarning() {
        // Given
        int entriesAdded = 4;
        int entriesSkipped = 0;
        int duplicatesAvoided = 1;
        String formattedDuration = "0.045";

        // When
        String result = clockData.formatClockDataMessage(entriesAdded, entriesSkipped, duplicatesAvoided, formattedDuration);

        // Then
        String message = """
                    ADVERTENCIA: El archivo fue procesado por el sistema correctamente pero presentó información duplicada.
                    
                    RESULTADOS:
                    4 entradas fueron añadidas.
                    1 entradas ya existían en la base de datos y no fueron añadidas para evitar duplicación.
                    
                    Tiempo de procesamiento: 0.045 segundos""";
        Assertions.assertEquals(message, result);
    }

    // 8. Entradas creadas, evitadas y duplicadas
    @Test
    void whenEntriesAddedSkippedAndDuplicatesAvoided_thenAddedWithFormatAndDuplicateWarning() {
        // Given
        int entriesAdded = 2;
        int entriesSkipped = 3;
        int duplicatesAvoided = 1;
        String formattedDuration = "0.051";

        // When
        String result = clockData.formatClockDataMessage(entriesAdded, entriesSkipped, duplicatesAvoided, formattedDuration);

        // Then
        String message = """
                    ADVERTENCIA: El archivo fue procesado por el sistema pero presentó errores.
                    
                    RESULTADOS:
                    2 entradas añadidas correctamente.
                    3 entradas sin añadir debido a errores de formato.
                    1 entradas ya existían en la base de datos y no fueron añadidas para evitar duplicación.
                    
                    Tiempo de procesamiento: 0.051 segundos""";
        Assertions.assertEquals(message, result);
    }

    /**
     * Tests for analyzeClockData
     **/

//    @Test
//    void whenEmployeeArrivesOnTime_thenNoDiscount() {
//        // Given
//        ClockDataEntity onTime = new ClockDataEntity(1L, Date.valueOf("2024-08-17"), Time.valueOf("08:00:00"), "12.345.678-9");
//        when(clockDataRepository.findAll()).thenReturn(List.of(onTime));
//
//        // When
//        clockData.analyzeClockData();
//
//        // Then
//        // Verifica que el método calculateDiscount nunca es llamado porque el empleado llegó a tiempo
//        verify(discountService, never()).calculateDiscount(anyString(), any(Date.class), any(Time.class), anyLong());
//        // Verifica que el método calculateExtraHours nunca es llamado porque el empleado no presenta horas extra
//        verify(extraHoursService, never()).calculateExtraHours(anyString(), any(Date.class), any(Time.class), anyLong());
//    }
//
//    @Test
//    void whenEmployeeArrivesLateButWithin10Minutes_thenNoDiscount() {
//        // Given
//        ClockDataEntity lateButWithin10Minutes = new ClockDataEntity(1L, Date.valueOf("2024-08-17"), Time.valueOf("08:05:00"), "12.345.678-9");
//        when(clockDataRepository.findAll()).thenReturn(List.of(lateButWithin10Minutes));
//
//        // When
//        clockData.analyzeClockData();
//
//        // Then
//        // Verifica que el método calculateDiscount nunca es llamado porque el empleado llegó a tiempo
//        verify(discountService, never()).calculateDiscount(anyString(), any(Date.class), any(Time.class), anyLong());
//        // Verifica que el método calculateExtraHours nunca es llamado porque el empleado no presenta horas extra
//        verify(extraHoursService, never()).calculateExtraHours(anyString(), any(Date.class), any(Time.class), anyLong());
//    }
//
//    @Test
//    void whenLateMoreThan10Minutes_thenCalculateDiscount() {
//        // Given
//        ClockDataEntity lateMoreThan10Minutes = new ClockDataEntity(1L, Date.valueOf("2024-08-17"), Time.valueOf("08:15:00"), "12.345.678-9");
//        when(clockDataRepository.findAll()).thenReturn(List.of(lateMoreThan10Minutes));
//
//        // When
//        clockData.analyzeClockData();
//
//        // Then
//        // Verifica que el método calculateDiscount sí es llamado porque el empleado presenta un atraso mayor a 10 minutos
//        verify(discountService).calculateDiscount(lateMoreThan10Minutes.getRut(), lateMoreThan10Minutes.getDate(), lateMoreThan10Minutes.getTime(), 15L);
//    }
//
//    @Test
//    void whenEmployeeWorksLate_thenCalculateExtraHours() {
//        // Given
//        ClockDataEntity extraHours = new ClockDataEntity(1L, Date.valueOf("2024-08-17"), Time.valueOf("19:00:00"), "12.345.678-9");
//        when(clockDataRepository.findAll()).thenReturn(List.of(extraHours));
//
//        // When
//        clockData.analyzeClockData();
//
//        // Then
//        // Verifica que el método calculateExtraHours sí es llamado porque el empleado trabajó 60 minutos adicionales
//        verify(extraHoursService).calculateExtraHours(extraHours.getRut(), extraHours.getDate(), extraHours.getTime(), 60L);
//    }

    /**
     * Tests for checkExtraHours
     **/

    @Test
    void whenEmployeeLeavesEarly_thenExtraHoursIsFalse() {
        Time exitTime = Time.valueOf("17:00:00");
        assertThat(clockData.checkExtraHours(exitTime)).isFalse();
    }

    @Test
    void whenEmployeeLeavesOnTime_thenExtraHoursIsFalse() {
        Time exitTime = Time.valueOf("18:00:00");
        assertThat(clockData.checkExtraHours(exitTime)).isFalse();
    }

    @Test
    void whenEmployeeLeavesAfterExitTime_thenExtraHoursIsTrue() {
        Time exitTime = Time.valueOf("19:00:00");
        assertThat(clockData.checkExtraHours(exitTime)).isTrue();
    }

    /**
     * Tests for checkLateArrival
     **/
    @Test
    void whenEmployeeArrivesEarly_thenLateArrivalIsFalse() {
        Time arrivalTime = Time.valueOf("07:59:00");
        assertThat(clockData.checkLateArrival(arrivalTime)).isFalse();
    }

    @Test
    void whenEmployeeArrivesOnTime_thenLateArrivalIsFalse() {
        Time arrivalTime = Time.valueOf("08:00:00");
        assertThat(clockData.checkLateArrival(arrivalTime)).isFalse();
    }

    @Test
    void whenEmployeeArrivesLate_thenLateArrivalIsTrue() {
        Time arrivalTime = Time.valueOf("08:20:00"); // 20 minutos tarde
        assertThat(clockData.checkLateArrival(arrivalTime)).isTrue();
    }

    /**
     * Tests for calculateLateMinutes
     **/
    @Test
    void whenEmployeeIsOnTimeCalculateMinutes_thenLateMinutesIsZero() {
        LocalTime arrivalTime = LocalTime.of(8, 0, 0);
        assertThat(clockData.calculateLateMinutes(arrivalTime)).isEqualTo(0);
    }

    @Test
    void whenEmployeeIsLateByMinutes_thenLateMinutesIsCorrect() {
        LocalTime arrivalTime = LocalTime.of(8, 15, 0); // 15 minutos tarde
        assertThat(clockData.calculateLateMinutes(arrivalTime)).isEqualTo(15);
    }

    /**
     * Tests for calculateExtraMinutes
     **/
    @Test
    void whenEmployeeLeavesOnTime_thenExtraMinutesIsZero() {
        LocalTime exitTime = LocalTime.of(18, 0, 0);
        assertThat(clockData.calculateExtraMinutes(exitTime)).isEqualTo(0);
    }

    @Test
    void whenEmployeeLeaves90MinutesAfterExitTime_thenExtraMinutesIsCorrect() {
        LocalTime exitTime = LocalTime.of(19, 30, 0); // 90 minutos tarde
        assertThat(clockData.calculateExtraMinutes(exitTime)).isEqualTo(90);
    }
}
