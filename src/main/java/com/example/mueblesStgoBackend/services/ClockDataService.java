package com.example.mueblesStgoBackend.services;

import com.example.mueblesStgoBackend.entities.ClockDataEntity;
import com.example.mueblesStgoBackend.repositories.ClockDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;

@Service
public class ClockDataService {
    @Autowired
    private ClockDataRepository clockDataRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ExtraHoursService extraHoursService;

    @Autowired
    private DiscountService discountService;

    public boolean fileTypeValidation(MultipartFile file) {
        if (file == null || file.getOriginalFilename() == null) {
            return false;
        }
        String fileName = file.getOriginalFilename();
        return fileName.toLowerCase().endsWith(".txt");
    }

    public boolean fileDataFormatValidation(String[] stringArray) {
        if (stringArray == null) {
            return false;
        }
        return stringArray.length == 3;
    }

    public boolean dateFormatValidation(String date) {
        String dateRegex = "^[0-9]{4}/[0-9]{2}/[0-9]{2}$";

        if (date.matches(dateRegex)) {
            String[] dateString = date.split("/");
            int month = Integer.parseInt(dateString[1]);
            int day = Integer.parseInt(dateString[2]);
            return month >= 1 && month <= 12 && day >= 1 && day <= 31;
        }
        return false;
    }

    public boolean timeFormatValidation(String time) {
        String timeRegex = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$";
        return time != null && time.matches(timeRegex);
    }

    public boolean doesEntryExist(Date date, Time time, String rut) {
        return clockDataRepository.findEntry(date, time, rut) != null;
    }

    public ResponseEntity<String> fileReader(MultipartFile file) {

        long startTime = System.currentTimeMillis(); // Start timing

        if (!fileTypeValidation(file)) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Only .txt files are supported.");
        }

        int entriesAdded = 0;
        int entriesSkipped = 0;
        int duplicatesAvoided = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int lineCounter = 0;
            while ((line = reader.readLine()) != null) {
                lineCounter++;
                String[] lineToString = line.split(";");

                if (!fileDataFormatValidation(lineToString)) {
                    entriesSkipped++;
                    continue;
                }

                if (!dateFormatValidation(lineToString[0])) {
                    entriesSkipped++;
                    continue;
                }

                if (!timeFormatValidation(lineToString[1])) {
                    entriesSkipped++;
                    continue;
                }

                if (!employeeService.rutFormatValidation(lineToString[2])) {
                    entriesSkipped++;
                    continue;
                }

                Date date = Date.valueOf(lineToString[0].replace("/", "-"));
                Time time = Time.valueOf(lineToString[1] + ":00");
                String rut = lineToString[2];
                if (doesEntryExist(date, time, rut)) {
                    duplicatesAvoided++;
                    continue;
                }

                saveClockData(date, time, rut);
                entriesAdded++;
            }

            long endTime = System.currentTimeMillis();
            long durationMilli = endTime - startTime;
            double durationSeconds = durationMilli / 1000.0;

            String formattedDuration = String.format("%.3f", durationSeconds).replace(",", "."); // Reemplaza , por .

            return ResponseEntity.status(HttpStatus.OK).body(
                    formatClockDataMessage(entriesAdded, entriesSkipped, duplicatesAvoided, formattedDuration));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading the file.");
        }
    }

    public String formatClockDataMessage(int entriesAdded, int entriesSkipped, int duplicatesAvoided, String formattedDuration) {
        String message = "";

        // CASO EXITOSO
        if (entriesAdded != 0 && entriesSkipped == 0 && duplicatesAvoided == 0) {
            message = String.format("""
                            RESULTADO: %d entradas añadidas.
                            
                            Tiempo de procesamiento: %s segundos""",
                    entriesAdded, formattedDuration);
        }

        // ERROR: ARCHIVO VACÍO O CON ERRORES
        if (entriesAdded == 0 && entriesSkipped == 0 && duplicatesAvoided == 0) {
            message = String.format("""
                            ERROR: El archivo no contiene información válida.
                            
                            Tiempo de procesamiento: %s segundos""",
                    formattedDuration);
        }

        // ERROR: ENTRADAS EVITADAS POR ERRORES DE FORMATO
        if (entriesAdded == 0 && entriesSkipped != 0 && duplicatesAvoided == 0) {
            message = String.format("""
                            ERROR: El archivo no contiene información válida.
                            
                            Tiempo de procesamiento: %s segundos""",
                    formattedDuration);
        }

        // ADVERTENCIA: ENTRADAS CREADAS Y ENTRADAS EVITADAS
        if (entriesAdded != 0 && entriesSkipped != 0 && duplicatesAvoided == 0) {
            message = String.format("""
                            ADVERTENCIA: El archivo fue procesado por el sistema pero presentó errores.
                            
                            RESULTADOS:
                            %d entradas añadidas correctamente.
                            %d entradas sin añadir debido a errores de formato.
                            
                            Tiempo de procesamiento: %s segundos""",
                    entriesAdded, entriesSkipped, formattedDuration);
        }

        // ADVERTENCIA: ENTRADAS DUPLICADAS
        if (entriesAdded == 0 && entriesSkipped == 0 && duplicatesAvoided != 0) {
            message = String.format("""
                            ADVERTENCIA: La información contenida en el archivo ya existe en el sistema.
                            
                            %d entradas fueron añadidas.
                            %d entradas ya existían en la base de datos y no fueron añadidas para evitar duplicación.
                            
                            Tiempo de procesamiento: %s segundos""",
                    entriesAdded, duplicatesAvoided, formattedDuration);
        }

        // ADVERTENCIA: ENTRADAS EVITADAS Y DUPLICADAS
        if (entriesAdded == 0 && entriesSkipped != 0 && duplicatesAvoided != 0) {
            message = String.format("""
                            ADVERTENCIA: El archivo fue procesado por el sistema pero presentó errores.
                            
                            RESULTADOS:
                            %d entradas sin añadir debido a errores de formato.
                            %d entradas ya existían en la base de datos y no fueron añadidas para evitar duplicación.
                            
                            Tiempo de procesamiento: %s segundos""",
                    entriesSkipped, duplicatesAvoided, formattedDuration);
        }

        // ADVERTENCIA: ENTRADAS AÑADIDAS CON ENTRADAS DUPLICADAS
        if (entriesAdded != 0 && entriesSkipped == 0 && duplicatesAvoided != 0) {
            message = String.format("""
                            ADVERTENCIA: El archivo fue procesado por el sistema correctamente pero presentó información duplicada.
                            
                            RESULTADOS:
                            %d entradas fueron añadidas.
                            %d entradas ya existían en la base de datos y no fueron añadidas para evitar duplicación.
                            
                            Tiempo de procesamiento: %s segundos""",
                    entriesAdded, duplicatesAvoided, formattedDuration);
        }

        // ADVERTENCIA: ENTRADAS CREADAS, EVITADAS Y DUPLICADAS
        if (entriesAdded != 0 && entriesSkipped != 0 && duplicatesAvoided != 0) {
            message = String.format("""
                            ADVERTENCIA: El archivo fue procesado por el sistema pero presentó errores.
                            
                            RESULTADOS:
                            %d entradas añadidas correctamente.
                            %d entradas sin añadir debido a errores de formato.
                            %d entradas ya existían en la base de datos y no fueron añadidas para evitar duplicación.
                            
                            Tiempo de procesamiento: %s segundos""",
                    entriesAdded, entriesSkipped, duplicatesAvoided, formattedDuration);
        }

        return message;
    }

    private void saveClockData(Date date, Time time, String rut) {
        ClockDataEntity clockData = new ClockDataEntity();
        clockData.setDate(date);
        clockData.setTime(time);
        clockData.setRut(rut);
        clockData.setProcessed(false);
        clockDataRepository.save(clockData);
    }

    public boolean isThereClockDataForYearAndMonth(int year, int month) {
        List<ClockDataEntity> clockData = clockDataRepository.findAllByYearAndMonth(year, month);
        return clockData != null && !clockData.isEmpty();
    }

    public boolean isThereUnprocessedDataForYearAndMonth(int year, int month) {
        List<ClockDataEntity> clockData = clockDataRepository.findAllUnprocessedByYearAndMonth(year, month);
        return clockData != null && !clockData.isEmpty();
    }

    public void analyzeClockData(int year, int month){
        List<ClockDataEntity> clockData = clockDataRepository.findAllByYearAndMonth(year, month);

        for (ClockDataEntity cd : clockData) {
            String rut = cd.getRut();
            Date date = cd.getDate();
            Time time = cd.getTime();

            if (employeeService.doesRutExists(rut)) {
                // Comprueba si el empleado tuvo un atraso
                if (checkLateArrival(time)) {
                    // Calcula cuántos minutos de atraso se produjeron
                    long minutes = calculateLateMinutes(time.toLocalTime());
                    // Calcula el descuento correspondiente
                    if (minutes > 10) {
                        discountService.calculateDiscount(rut, date, time, minutes);
                    }
                }

                // Comprueba si aplican horas extras
                if (checkExtraHours(time)) {
                    // Calcula cuántos minutos después de la salida oficial se trabajaron
                    long extraHoursMinutes = calculateExtraMinutes(time.toLocalTime());
                    // Calcula el monto correspondiente al pago de horas extra
                    extraHoursService.calculateExtraHours(rut, date, time, extraHoursMinutes);
                }

                cd.setProcessed(true);
                clockDataRepository.save(cd);
            }
        }
    }

    public boolean checkExtraHours(Time exit) {
        LocalTime exitTime = LocalTime.of(18, 0, 0);
        LocalTime realExitTime = exit.toLocalTime();
        return realExitTime.isAfter(exitTime);
    }

    public boolean checkLateArrival(Time arrival) {
        LocalTime entryTime = LocalTime.of(8, 0, 0);
        LocalTime exitTime = LocalTime.of(18, 0, 0);
        LocalTime arrivalTime = arrival.toLocalTime();
        return arrivalTime.isAfter(entryTime) && arrivalTime.isBefore(exitTime);
    }

    public long calculateLateMinutes(LocalTime arrivalTime) {
        LocalTime entryTime = LocalTime.of(8, 0, 0);
        if (arrivalTime.isAfter(entryTime)) {
            return java.time.Duration.between(entryTime, arrivalTime).toMinutes();
        }
        return 0;
    }

    public long calculateExtraMinutes(LocalTime exitTime) {
        LocalTime endTime = LocalTime.of(18, 0, 0);
        if (exitTime.isAfter(endTime)) {
            return java.time.Duration.between(endTime, exitTime).toMinutes();
        }
        return 0;
    }
}
