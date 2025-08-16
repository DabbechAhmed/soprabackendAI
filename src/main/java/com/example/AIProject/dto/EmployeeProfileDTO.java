package com.example.AIProject.dto;

import com.example.AIProject.enums.EducationLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeProfileDTO {
    private Long id;
    private Long userId;
    private String userEmail;
    private String userName;
    private String cvText;
    private Integer experienceYears;
    private EducationLevel education;
    private String skills;
    private String softSkills;
    private String country;
    private String city;
    private BigDecimal preferredSalaryMin;
    private BigDecimal preferredSalaryMax;
    private Boolean profileComplete;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}