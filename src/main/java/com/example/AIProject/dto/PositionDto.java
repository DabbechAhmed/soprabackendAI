package com.example.AIProject.dto;

import com.example.AIProject.enums.ContractType;
import com.example.AIProject.enums.EducationLevel;
import com.example.AIProject.enums.MobilityType;
import com.example.AIProject.enums.PositionStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PositionDto {
    private Long id;
    private String title;
    private String department;
    private String description;
    private String requirements;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private ContractType contractType;
    private Integer experienceRequired;
    private EducationLevel educationRequired;
    private PositionStatus status;
    private MobilityType mobilityType;
    private Long targetBranchId;
    private String targetBranchName;
    private String country;
    private String city;
    private String externalHrContact;
    private String externalHrEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer applicationsCount;
}
