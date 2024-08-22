package com.employee.controller;

import com.employee.entity.Employee;
import com.employee.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class EmployeeControllerTests {

    @InjectMocks
    private EmployeeController employeeController;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddEmployee_Success() {
        Employee employee = new Employee();
        employee.setEmployeeId("E123");
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john.doe@example.com");
        employee.setPhoneNumbers(Arrays.asList("1234567890", "0987654321"));
        employee.setDoj(LocalDate.of(2023, 5, 16));
        employee.setSalary((double) 50000);

        when(bindingResult.hasErrors()).thenReturn(false);
        doNothing().when(employeeService).saveEmployee(employee);

        ResponseEntity<?> response = employeeController.addEmployee(employee, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Employee saved successfully", response.getBody());
    }

    @Test
    void testAddEmployee_ValidationError() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(
                Arrays.asList(new ObjectError("field", "Error message"))
        );

        Employee employee = new Employee(); // Employee details are not important here

        ResponseEntity<?> response = employeeController.addEmployee(employee, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        List<String> errors = (List<String>) response.getBody();
        assertEquals(1, errors.size());
        assertEquals("Error message", errors.get(0));
    }

    @Test
    void testGetTaxDeductions_EmployeeFound() {
        Employee employee = new Employee();
        employee.setEmployeeId("E123");
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setSalary((double) 50000);
        employee.setDoj(LocalDate.of(2023, 5, 16));

        when(employeeService.getEmployeeById("E123")).thenReturn(Optional.of(employee));
        when(employeeService.calculateTax(50000)).thenReturn(37500.0);

        ResponseEntity<?> response = employeeController.getTaxDeductions("E123");

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("employeeId", "E123");
        expectedResponse.put("firstName", "John");
        expectedResponse.put("lastName", "Doe");
        expectedResponse.put("yearlySalary", 50000.0);
        expectedResponse.put("taxAmount", 37500.0);
        expectedResponse.put("cessAmount", 0.0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void testGetTaxDeductions_EmployeeNotFound() {
        when(employeeService.getEmployeeById("E123")).thenReturn(Optional.empty());

        ResponseEntity<?> response = employeeController.getTaxDeductions("E123");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Employee not found", response.getBody());
    }
}
