package com.employee.repository;

import com.employee.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class EmployeeRepositoryTests {

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    public void setUp() {
        // Set up any necessary preconditions here, if needed.
    }

    @Test
    public void testExistsByEmail_True() {
        Employee employee = new Employee("1", "John", "Doe", 50000, LocalDate.now());
        employee.setEmail("john.doe@example.com");
        employeeRepository.save(employee);

        boolean exists = employeeRepository.existsByEmail("john.doe@example.com");

        assertTrue(exists);
    }

    @Test
    public void testExistsByEmail_False() {
        boolean exists = employeeRepository.existsByEmail("nonexistent@example.com");

        assertFalse(exists);
    }

    @Test
    public void testFindById_Found() {
        Employee employee = new Employee("1", "John", "Doe", 50000, LocalDate.now());
        employeeRepository.save(employee);

        Optional<Employee> foundEmployee = employeeRepository.findById("1");

        assertTrue(foundEmployee.isPresent());
        assertEquals("John", foundEmployee.get().getFirstName());
    }

    @Test
    public void testFindById_NotFound() {
        Optional<Employee> foundEmployee = employeeRepository.findById("nonexistent");

        assertFalse(foundEmployee.isPresent());
    }

    @Test
    public void testSaveEmployee() {
        Employee employee = new Employee("1", "Jane", "Doe", 60000, LocalDate.of(2023, 1, 1));
        Employee savedEmployee = employeeRepository.save(employee);

        assertNotNull(savedEmployee);
        assertEquals("1", savedEmployee.getEmployeeId());
    }
}
