package com.example.AIProject.requests.position;

import com.example.AIProject.enums.ContractType;
import com.example.AIProject.enums.EducationLevel;
import jakarta.validation.constraints.Positive;
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

    @Positive(message = "Le salaire minimum doit être positif")
    private BigDecimal salaryMin;

    @Positive(message = "Le salaire maximum doit être positif")
    private BigDecimal salaryMax;

    private String country;

    private String city;

    private ContractType contractType;

    @Positive(message = "L'expérience requise doit être positive")
    private Integer experienceRequired;

    private EducationLevel educationRequired;
}