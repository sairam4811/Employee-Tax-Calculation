package com.employee.service;

import java.time.LocalDate;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.employee.entity.Employee;
import com.employee.exception.EmployeeNotFoundException;
import com.employee.repository.EmployeeRepository;

@Service
public class EmployeeService {
	
	@Autowired
    private EmployeeRepository employeeRepository;

    public Employee saveEmployee(Employee employee) {
        if (employeeRepository.existsById(employee.getEmployeeId())) {
            throw new RuntimeException("Employee ID already exists");
        }
        return employeeRepository.save(employee);
    }
	
    public Map<String, Double> calculateTaxDeductions(Employee employee) {
        double yearlySalary = calculateYearlySalary(employee);
        double taxAmount = calculateTax(yearlySalary);
        double cessAmount = calculateCess(yearlySalary);

        Map<String, Double> taxDetails = new HashMap<>();
        taxDetails.put("yearlySalary", yearlySalary);
        taxDetails.put("taxAmount", taxAmount);
        taxDetails.put("cessAmount", cessAmount);
        return taxDetails;
    }

    private double calculateYearlySalary(Employee employee) {
        int monthsWorked = calculateMonthsWorked(employee.getDoj());
        return employee.getSalary() * monthsWorked;
    }

    private int calculateMonthsWorked(LocalDate doj) {
        LocalDate endOfFinancialYear = LocalDate.of(doj.getYear() + 1, 3, 31);
        return (int) ChronoUnit.MONTHS.between(doj, endOfFinancialYear);
    }

    public double calculateTax(double yearlySalary) {
        double tax = 0;
        if (yearlySalary > 1000000) {
            tax += (yearlySalary - 1000000) * 0.20;
            yearlySalary = 1000000;
        }
        if (yearlySalary > 500000) {
            tax += (yearlySalary - 500000) * 0.10;
            yearlySalary = 500000;
        }
        if (yearlySalary > 250000) {
            tax += (yearlySalary - 250000) * 0.05;
        }
        return tax;
    }

    double calculateCess(double yearlySalary) {
        if (yearlySalary > 2500000) {
            return (yearlySalary - 2500000) * 0.02;
        }
        return 0;
    }

    public Optional<Employee> getEmployeeById(String employeeId) {
        return Optional.ofNullable(employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId)));
    }

}

