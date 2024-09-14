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
import java.util.Objects;

@Service
public class ClockDataService {

    @Autowired
    private ClockDataRepository clockDataRepository;

    @Autowired
    private EmployeeService employeeService;

    public boolean fileTypeValidation(MultipartFile file) {
        String fileName = Objects.requireNonNull(file.getOriginalFilename());
        return fileName.toLowerCase().endsWith(".txt");
    }

    public boolean fileDataFormatValidation(String[] stringArray) {
        return stringArray.length == 3;
    }

    public boolean dateFormatValidation(String date) {
        if (date.contains("/")) {
            String[] dateString = date.split("/");
            if (dateString.length == 3) {
                return dateString[0].length() == 4 && dateString[1].length() == 2 && dateString[2].length() == 2;
            }
        }
        return false;
    }

    public boolean timeFormatValidation(String time) {
        if (time.contains(":")) {
            String[] timeString = time.split(":");
            if (timeString.length == 2) {
                return timeString[0].length() == 2 && timeString[1].length() == 2;
            }
        }
        return false;
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
                //System.out.println(line);
                String[] lineToString = line.split(";");

                if (!fileDataFormatValidation(lineToString)) {
                    System.out.println("Error: File data format denied in line " + lineCounter + " containing: \"" + line + "\".");
                    entriesSkipped++;
                    continue;
                }

                if (!dateFormatValidation(lineToString[0])) {
                    System.out.println("Error: Date format denied in line " + lineCounter + " containing: \"" + line + "\".");
                    entriesSkipped++;
                    continue;
                }

                if (!timeFormatValidation(lineToString[1])) {
                    System.out.println("Error: Time format denied in line " + lineCounter + " containing: \"" + line + "\".");
                    entriesSkipped++;
                    continue;
                }

                if (!employeeService.rutFormatValidation(lineToString[2])) {
                    System.out.println("Error: Rut format denied in line " + lineCounter + " containing: \"" + line + "\".");
                    entriesSkipped++;
                    continue;
                }

                Date date = Date.valueOf(lineToString[0].replace("/", "-"));
                Time time = Time.valueOf(lineToString[1] + ":00");
                String rut = lineToString[2];
                if (doesEntryExist(date, time, rut)) {
                    System.out.println("Duplication avoided: The data of line " + lineCounter + " already exists in the database.");
                    duplicatesAvoided++;
                    continue;
                }

                saveClockData(date, time, rut);
                entriesAdded++;
            }

            long endTime = System.currentTimeMillis(); // End timing
            long durationMilli = endTime - startTime; // Calculate duration
            double durationSeconds = durationMilli / 1000.0; // Milli -> Seconds

            String formattedDuration = String.format("%.3f", durationSeconds).replace(",", "."); // Replace , for .

            String message = String.format("""
                            File processed successfully.
                            %d entries added, %d entries skipped due to data format errors, and %d duplicate entries avoided.
                            Processing time: %s seconds""",
                    entriesAdded, entriesSkipped, duplicatesAvoided, formattedDuration);

            return ResponseEntity.status(HttpStatus.OK).body(message);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading the file.");
        }
    }

    private void saveClockData(Date date, Time time, String rut) {
        ClockDataEntity clockData = new ClockDataEntity();
        clockData.setDate(date);
        clockData.setTime(time);
        clockData.setRut(rut);
        clockDataRepository.save(clockData);
    }

}
