package com.example.AIProject.requests.employeeProfile;

import com.example.AIProject.enums.EducationLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
public class CreateEmployeeProfileRequest {
    @NotNull
    private Long userId;

    @NotBlank
    private String cvText;

    @NotNull
    private Integer experienceYears;

    @NotNull
    private EducationLevel education;

    @NotBlank
    private String skills;

    @NotBlank
    private String country;

    @NotBlank
    private String city;

    private BigDecimal preferredSalaryMin;

    private BigDecimal preferredSalaryMax;
}