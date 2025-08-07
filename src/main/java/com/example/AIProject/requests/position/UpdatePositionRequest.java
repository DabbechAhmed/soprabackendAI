package com.example.AIProject.requests.position;

import com.example.AIProject.enums.ContractType;
import com.example.AIProject.enums.EducationLevel;
import com.example.AIProject.enums.MobilityType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdatePositionRequest {
    private String title;
    private String department;
    private String description;
    private String requirements;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private ContractType contractType;
    private Integer experienceRequired;
    private EducationLevel educationRequired;

    // NOUVEAU: Pour la mobilité
    private MobilityType mobilityType;
    private Long targetBranchId;
    private String externalHrContact;
    private String externalHrEmail;
}