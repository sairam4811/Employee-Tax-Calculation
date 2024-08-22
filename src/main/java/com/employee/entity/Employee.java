package com.employee.entity;

import java.time.LocalDate;


import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class Employee {
	
	public Employee(String string, String string2, String string3, double d, LocalDate now) {
		// TODO Auto-generated constructor stub
	}

	@Id
	@Pattern(regexp = "E\\d{3}", message = "Employee ID must be in the format 'E123'")
	@NotNull(message = "Employee ID is mandatory")
    private String employeeId;

	@NotNull(message = "First name is mandatory")
	@NotBlank(message = "First name is mandatory")
    private String firstName;

	@NotNull(message = "Last name is mandatory")
	@NotBlank(message = "First name is mandatory")
    private String lastName;

	@Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private String email;

	@ElementCollection
    @CollectionTable(name = "employee_phone_numbers", joinColumns = @JoinColumn(name = "employee_id"))
	@Column(name = "phone_number")
	@Size(min = 1, message = "At least one phone number is required")
	@NotEmpty(message = "Phone number list cannot be empty")
    private List<@Pattern(regexp = "\\d{10}", message = "Phone number should be a valid 10-digit number") String> phoneNumbers;

	@NotNull(message = "Date of joining is mandatory")
	@PastOrPresent(message = "Date of joining cannot be in the future")
    private LocalDate doj;

	@NotNull(message = "Salary is mandatory")
	@Positive(message = "Salary must be a positive number")
    private Double salary;

}
