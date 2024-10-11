package com.example.mueblesStgoBackend.services;

import com.example.mueblesStgoBackend.entities.ExtraHoursAuthorizationEntity;
import com.example.mueblesStgoBackend.entities.ExtraHoursEntity;
import com.example.mueblesStgoBackend.repositories.ExtraHoursAuthorizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class ExtraHoursAuthorizationService {

    @Autowired
    ExtraHoursAuthorizationRepository extraHoursAuthorizationRepository;

    @Autowired
    ExtraHoursService extraHoursService;

    public ExtraHoursAuthorizationEntity findAuthorization(String rut, Date date) {
        return extraHoursAuthorizationRepository.findByRutAndDate(rut, date);
    }

    public List<ExtraHoursAuthorizationEntity> getAllByYearAndMonth(int year, int month) {
        return extraHoursAuthorizationRepository.findAllByYearAndMonth(year, month);
    }

    public ResponseEntity<String> addAuthorization(ExtraHoursAuthorizationEntity auth) {
        if (findAuthorization(auth.getRut(), auth.getDate()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflicto: Una autorización para esta persona y fecha ya ha sido ingresada");
        }
        else {
            extraHoursAuthorizationRepository.save(auth);
            return ResponseEntity.ok("Autorización ingresada correctamente.");
        }
    }

    public void authorizeExtraHoursByYearAndMonth(int year, int month) {
        List<ExtraHoursEntity> extraHours = extraHoursService.getAllByYearAndMonth(year, month);

        for (ExtraHoursEntity extraHour : extraHours) {
            ExtraHoursAuthorizationEntity authorized = findAuthorization(extraHour.getRut(), extraHour.getDate());
            if (authorized != null) {
                authorizeExtraHours(authorized.getRut(), year, month);
            }
        }
    }

    public void authorizeExtraHours(String rut, int year, int month) {
        ExtraHoursEntity extraHour = extraHoursService.getByRutAndYearAndMonth(rut, year, month);
        if (extraHour != null) {
            extraHoursService.updateAuthorization(extraHour, true);
        }
    }
}
