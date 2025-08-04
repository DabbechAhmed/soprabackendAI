package com.example.AIProject.requests.employeeProfile;

import com.example.AIProject.enums.EducationLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
public class UpdateEmployeeProfileRequest {
    private String cvText;

    private Integer experienceYears;

    private EducationLevel education;

    private String skills;

    private String country;

    private String city;

    private BigDecimal preferredSalaryMin;

    private BigDecimal preferredSalaryMax;
}