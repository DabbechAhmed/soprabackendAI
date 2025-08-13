package com.example.AIProject.requests.employeeProfile;

import com.example.AIProject.enums.EducationLevel;
import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

@Data
public class UpdateEmployeeProfileRequest {

    @Size(max = 10000, message = "CV text must not exceed 10000 characters")
    private String cvText;

    @Min(value = 0, message = "Experience years must be positive")
    private Integer experienceYears;

    private EducationLevel education;

    @Size(max = 2000, message = "Skills must not exceed 2000 characters")
    private String skills;

    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @DecimalMin(value = "0.0", message = "Minimum salary must be positive")
    private BigDecimal preferredSalaryMin;

    @DecimalMin(value = "0.0", message = "Maximum salary must be positive")
    private BigDecimal preferredSalaryMax;
}