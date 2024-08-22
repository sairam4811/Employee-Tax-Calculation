package com.employee.controller;

import java.time.LocalDate;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.employee.entity.Employee;
import com.employee.service.EmployeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/employees")
@Validated
public class EmployeeController {
	
	@Autowired
	private EmployeeService employeeService;
	
	@Operation(summary = "Add a new employee",
            description = "Stores the details of a new employee. Validates all fields and returns appropriate error messages if the data is invalid.",
            responses = {
                @ApiResponse(responseCode = "200", description = "Employee saved successfully"),
                @ApiResponse(responseCode = "400", description = "Validation errors",
                    content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = List.class)))
            })
	@PostMapping
	public ResponseEntity<?> addEmployee(@Valid @RequestBody Employee employee, BindingResult bindingResult) {
	    if (bindingResult.hasErrors()) {
	        List<String> errors = bindingResult.getAllErrors().stream()
	                .map(ObjectError::getDefaultMessage)
	                .collect(Collectors.toList());
	        
	        return ResponseEntity.badRequest().body(errors);
	    }
	    
	    employeeService.saveEmployee(employee);
	    return ResponseEntity.ok("Employee saved successfully");
	}

	 @Operation(summary = "Get tax deductions for an employee",
             description = "Returns tax deduction details for an employee for the current financial year (April to March). Calculates deductions based on employee's salary and date of joining.",
             responses = {
                 @ApiResponse(responseCode = "200", description = "Tax deduction details",
                     content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = Map.class))),
                 @ApiResponse(responseCode = "404", description = "Employee not found")
             })
    @GetMapping("/{employeeId}/tax-deductions")
    public ResponseEntity<?> getTaxDeductions(@PathVariable String employeeId) {
        Optional<Employee> employeeOpt = employeeService.getEmployeeById(employeeId);

        if (!employeeOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
        }

        Employee employee = employeeOpt.get();

       
        LocalDate doj = employee.getDoj();
        LocalDate now = LocalDate.now();

        LocalDate financialYearStart = now.getMonthValue() < 4 ? LocalDate.of(now.getYear() - 1, 4, 1) : LocalDate.of(now.getYear(), 4, 1);
        LocalDate effectiveStart = doj.isBefore(financialYearStart) ? financialYearStart : doj;
        long monthsWorked = ChronoUnit.MONTHS.between(effectiveStart.withDayOfMonth(1), now.withDayOfMonth(1)) + 1;

        double yearlySalary = employee.getSalary() * monthsWorked;
        double taxAmount = employeeService.calculateTax(yearlySalary); 
        double cessAmount = yearlySalary > 2500000 ? (yearlySalary - 2500000) * 0.02 : 0;

        
        Map<String, Object> response = new HashMap<>();
        response.put("employeeId", employee.getEmployeeId());
        response.put("firstName", employee.getFirstName());
        response.put("lastName", employee.getLastName());
        response.put("yearlySalary", yearlySalary);
        response.put("taxAmount", taxAmount);
        response.put("cessAmount", cessAmount);

        return ResponseEntity.ok(response);
    }

	
}
