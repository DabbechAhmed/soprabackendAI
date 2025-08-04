package com.example.AIProject.requests.position;

import com.example.AIProject.enums.ContractType;
import com.example.AIProject.enums.EducationLevel;
import com.example.AIProject.enums.MobilityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreatePositionRequest {
    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    @NotBlank(message = "Le département est obligatoire")
    private String department;

    private String description;
    private String requirements;

    @Positive(message = "Le salaire minimum doit être positif")
    private BigDecimal salaryMin;

    @Positive(message = "Le salaire maximum doit être positif")
    private BigDecimal salaryMax;

    @NotNull(message = "Le type de contrat est obligatoire")
    private ContractType contractType;

    @NotNull(message = "L'expérience requise est obligatoire")
    @Positive(message = "L'expérience requise doit être positive")
    private Integer experienceRequired;

    private EducationLevel educationRequired;

    @NotNull(message = "Le type de mobilité est obligatoire")
    private MobilityType mobilityType;

    @NotNull(message = "La branche cible est obligatoire")
    private Long targetBranchId;

    // Pour mobilité externe uniquement
    private String externalHrContact;
    private String externalHrEmail;
}