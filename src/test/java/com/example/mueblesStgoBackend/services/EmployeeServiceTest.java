package com.example.mueblesStgoBackend.services;

import com.example.mueblesStgoBackend.entities.EmployeeEntity;
import com.example.mueblesStgoBackend.repositories.EmployeeRepository;
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

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    EmployeeService employeeServiceTest = new EmployeeService();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Testing doesRutExists
     **/

    @Test
    void whenRutExists_thenReturnsTrue() {
        // Given
        String rut = "11.234.123-6";
        Date date = Date.valueOf("2000-09-15");

        // Empleado ficticio
        EmployeeEntity mockEmployee = new EmployeeEntity(1L, rut, "FirstName", "LastName", date, "Category", date);
        when(employeeRepository.findByRut(rut)).thenReturn(mockEmployee);

        // When
        boolean exists = employeeServiceTest.doesRutExists(rut);

        // Then
        assertThat(exists).isTrue();
    }


    @Test
    void whenRutDoesNotExist_thenReturnsFalse() {
        // Given
        String rut = "11.234.123-6";
        when(employeeRepository.findByRut(rut)).thenReturn(null);

        // When
        boolean exists = employeeServiceTest.doesRutExists(rut);

        // Then
        assertThat(exists).isFalse();
    }

    /**
     * Testing findByRut
     **/

    @Test
    void whenRutExists_thenReturnsEmployee() {
        // Given
        String rut = "11.234.123-6";
        EmployeeEntity employee = new EmployeeEntity();
        when(employeeRepository.findByRut(rut)).thenReturn(employee);

        // When
        EmployeeEntity foundEmployee = employeeServiceTest.findByRut(rut);

        // Then
        assertThat(foundEmployee).isEqualTo(employee);
    }

    @Test
    void whenRutDoesNotExist_thenReturnsNull() {
        // Given
        String rut = "11.234.123-6";
        when(employeeRepository.findByRut(rut)).thenReturn(null);

        // When
        EmployeeEntity foundEmployee = employeeServiceTest.findByRut(rut);

        // Then
        assertThat(foundEmployee).isNull();
    }

    /**
     * Testing rutFormatValidation
     **/

    @Test
    void whenRutIsValid_thenReturnsTrue() {
        // Given
        String validRut = "11.234.123-6"; // Rut válido

        // When
        boolean isValid = employeeServiceTest.rutFormatValidation(validRut);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void whenRutIsInvalid_thenReturnsFalse() {
        // Given
        String invalidRut = "112341236"; // Rut con formato no válido

        // When
        boolean isValid = employeeServiceTest.rutFormatValidation(invalidRut);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void whenRutIsTooShort_thenReturnsFalse() {
        // Given
        String shortRut = "11.234.123"; // Rut con formato inválido al faltar dígito verificador

        // When
        boolean isValid = employeeServiceTest.rutFormatValidation(shortRut);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void whenRutIsTooLong_thenReturnsFalse() {
        // Given
        String longRut = "11.234.123-65"; // Rut demasiado largo, contiene error numérico

        // When
        boolean isValid = employeeServiceTest.rutFormatValidation(longRut);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void whenRutHasOnlyNumbers_thenReturnsFalse() {
        // Given
        String rut = "112341236"; // Sin puntos ni guión, formato incorrecto

        // When
        boolean isValid = employeeServiceTest.rutFormatValidation(rut);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void whenRutHasIncorrectDotSymbolQuantity_thenReturnsFalse() {
        // Given
        String rut = "11.234.123.6"; // Símbolo de punto adicional reemplaza al guion

        // When
        boolean isValid = employeeServiceTest.rutFormatValidation(rut);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void whenRutHasIncorrectDashSymbolQuantity_thenReturnsFalse() {
        // Given
        String rut = "11.234.123--6"; // Símbolo de guion adicional

        // When
        boolean isValid = employeeServiceTest.rutFormatValidation(rut);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void whenRutHasIncorrectSymbolsPositions_thenReturnsFalse() {
        // Given
        String rut = "11.234-123.6"; // Posiciones incorrectas de los símbolos

        // When
        boolean isValid = employeeServiceTest.rutFormatValidation(rut);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void whenRutContainsInvalidCharacters_thenReturnsFalse() {
        // Given
        String rut = "11.234.12A-6"; // Contiene un caracter innecesario, en este caso una letra (A)

        // When
        boolean isValid = employeeServiceTest.rutFormatValidation(rut);

        // Then
        assertThat(isValid).isFalse();
    }

    /**
     * Testing addEmployee
     **/

    @Test
    void whenAddingNewEmployeeWithValidRut_thenReturnsSuccess() {
        // Given
        EmployeeEntity newEmployee = new EmployeeEntity();
        newEmployee.setRut("11.234.123-6");

        when(employeeRepository.findByRut(newEmployee.getRut())).thenReturn(null);
        when(employeeRepository.save(newEmployee)).thenReturn(newEmployee);

        // When
        ResponseEntity<String> response = employeeServiceTest.addEmployee(newEmployee);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Empleado creado correctamente.");
    }

    @Test
    void whenAddingNewEmployeeWithInvalidRutFormat_thenReturnsExpectationFailed() {
        // Given
        EmployeeEntity newEmployee = new EmployeeEntity();
        newEmployee.setRut("12345678"); // Formato de RUT no válido

        when(employeeRepository.findByRut(newEmployee.getRut())).thenReturn(null);

        boolean isValidRut = employeeServiceTest.rutFormatValidation(newEmployee.getRut());
        assertThat(isValidRut).isFalse();

        // When
        ResponseEntity<String> response = employeeServiceTest.addEmployee(newEmployee);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.EXPECTATION_FAILED);
        assertThat(response.getBody()).isEqualTo("El rut del empleado no existe o está incorrecto.");
    }

    @Test
    void whenAddingExistingEmployee_thenReturnsConflict() {
        // Given
        EmployeeEntity existingEmployee = new EmployeeEntity();
        existingEmployee.setRut("11.234.123-6");

        when(employeeRepository.findByRut(existingEmployee.getRut())).thenReturn(existingEmployee);

        // When
        ResponseEntity<String> response = employeeServiceTest.addEmployee(existingEmployee);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isEqualTo("El empleado ya existe en la base de datos");
    }


    /**
     * Testing getAll
     **/
    @Test
    void whenGetAllEmployees_thenReturnsListOfEmployees() {
        // Given
        EmployeeEntity employee1 = new EmployeeEntity(1L, "11.234.123-6", "Diego", "Figueroa González", Date.valueOf("2000-09-15"), "A", Date.valueOf("2023-01-01"));
        EmployeeEntity employee2 = new EmployeeEntity(2L, "22.345.234-7", "Ana", "Pérez Rojas", Date.valueOf("1990-05-10"), "B", Date.valueOf("2023-01-01"));
        List<EmployeeEntity> employees = List.of(employee1, employee2);

        when(employeeRepository.findAll()).thenReturn(employees);

        // When
        List<EmployeeEntity> foundEmployees = employeeServiceTest.getAll();

        // Then
        assertThat(foundEmployees).isEqualTo(employees);
    }

    /**
     * Testing getById
     **/
    @Test
    void whenEmployeeExistsById_thenReturnsEmployee() {
        // Given
        Long id = 1L;
        EmployeeEntity mockEmployee = new EmployeeEntity(id, "11.234.123-6", "Diego", "Figueroa González", Date.valueOf("2000-09-15"), "A", Date.valueOf("2023-01-01"));
        when(employeeRepository.findById(id)).thenReturn(Optional.of(mockEmployee));

        // When
        Optional<EmployeeEntity> foundEmployee = employeeServiceTest.getById(id);

        // Then
        assertThat(foundEmployee).isPresent();
        assertThat(foundEmployee.get()).isEqualTo(mockEmployee);
    }

    @Test
    void whenEmployeeDoesNotExistById_thenReturnsEmpty() {
        // Given
        Long id = 1L;
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<EmployeeEntity> foundEmployee = employeeServiceTest.getById(id);

        // Then
        assertThat(foundEmployee).isNotPresent();
    }

    /**
     * Testing delete
     **/
    @Test
    void whenDeleteEmployeeExists_thenReturnsSuccessMessage() {
        // Given
        Long id = 1L;
        EmployeeEntity mockEmployee = new EmployeeEntity(id, "11.234.123-6", "Diego", "Figueroa González", Date.valueOf("2000-09-15"), "A", Date.valueOf("2023-01-01"));
        when(employeeRepository.findById(id)).thenReturn(Optional.of(mockEmployee));

        // When
        ResponseEntity<String> response = employeeServiceTest.delete(id);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Empleado eliminado correctamente");
        verify(employeeRepository).deleteById(id);
    }

    @Test
    void whenDeleteEmployeeDoesNotExist_thenReturnsNotFoundMessage() {
        // Given
        Long id = 1L;
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        // When
        ResponseEntity<String> response = employeeServiceTest.delete(id);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("No existen empleados registrados con el id " + id + ".");
    }

    /**
     * Testing updateEmployee
     **/
    @Test
    void whenUpdateEmployeeExists_thenReturnsSuccessMessage() {
        // Given
        Long id = 1L;
        EmployeeEntity existingEmployee = new EmployeeEntity(id, "11.234.123-6", "Diego", "Figueroa González", Date.valueOf("2000-09-15"), "A", Date.valueOf("2023-01-01"));
        EmployeeEntity updatedEmployee = new EmployeeEntity(id, "11.234.123-6", "Diego", "Figueroa Guerra", Date.valueOf("1995-05-10"), "A", Date.valueOf("2023-01-01"));

        when(employeeRepository.findById(id)).thenReturn(Optional.of(existingEmployee));

        // When
        ResponseEntity<String> response = employeeServiceTest.updateEmployee(id, updatedEmployee);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Empleado creado correctamente");
        verify(employeeRepository).save(existingEmployee);
    }

    @Test
    void whenUpdateEmployeeDoesNotExist_thenReturnsNotFoundMessage() {
        // Given
        Long id = 1L;
        EmployeeEntity employee = new EmployeeEntity(id, "11.234.123-6", "Diego", "Figueroa González", Date.valueOf("1995-05-10"), "A", Date.valueOf("2023-01-01"));

        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        // When
        ResponseEntity<String> response = employeeServiceTest.updateEmployee(id, employee);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("No se pudo encontrar el empleado.");
    }

}
