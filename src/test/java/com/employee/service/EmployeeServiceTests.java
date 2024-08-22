package com.employee.service;

import com.employee.entity.Employee;
import com.employee.exception.EmployeeNotFoundException;
import com.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmployeeServiceTests {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveEmployee_Success() {
        Employee employee = new Employee("1", "John", "Doe", 50000, LocalDate.now());
        when(employeeRepository.existsById(employee.getEmployeeId())).thenReturn(false);
        when(employeeRepository.save(employee)).thenReturn(employee);

        Employee savedEmployee = employeeService.saveEmployee(employee);

        assertNotNull(savedEmployee);
        assertEquals(employee.getEmployeeId(), savedEmployee.getEmployeeId());
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    public void testSaveEmployee_Failure() {
        Employee employee = new Employee("E123", "John", "Doe", 50000d, LocalDate.now());
        when(employeeRepository.existsById(employee.getEmployeeId())).thenReturn(true);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            employeeService.saveEmployee(employee);
        });

        assertEquals("Employee ID already exists", thrown.getMessage());
        verify(employeeRepository, never()).save(employee);
    }

    @Test
    public void testCalculateTaxDeductions() {
        Employee employee = new Employee("E123", "John", "Doe", 50000, LocalDate.of(2022, 4, 1));
        Map<String, Double> expected = new HashMap<>();
        double yearlySalary = employee.getSalary() * 12;
        expected.put("yearlySalary", yearlySalary);
        expected.put("taxAmount", employeeService.calculateTax(yearlySalary));
        expected.put("cessAmount", employeeService.calculateCess(yearlySalary));

        Map<String, Double> taxDeductions = employeeService.calculateTaxDeductions(employee);

        assertEquals(expected, taxDeductions);
    }

    @Test
    public void testCalculateTax() {
        double salary = 1200000;
        double tax = employeeService.calculateTax(salary);
        assertEquals(200000, tax);
    }

    @Test
    public void testCalculateCess() {
        double salary = 3000000;
        double cess = employeeService.calculateCess(salary);
        assertEquals(10000, cess);
    }

    @Test
    public void testGetEmployeeById_Found() {
        Employee employee = new Employee("1", "John", "Doe", 50000, LocalDate.now());
        when(employeeRepository.findById("1")).thenReturn(Optional.of(employee));

        Employee foundEmployee = employeeService.getEmployeeById("1").get();

        assertEquals("1", foundEmployee.getEmployeeId());
        assertEquals("John", foundEmployee.getFirstName());
    }

    @Test
    public void testGetEmployeeById_NotFound() {
        when(employeeRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.getEmployeeById("1");
        });
    }
}
