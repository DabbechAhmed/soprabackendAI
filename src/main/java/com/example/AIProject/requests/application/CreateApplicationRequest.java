package com.example.AIProject.requests.application;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
public class CreateApplicationRequest {
    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    private Long userId;

    @NotNull(message = "L'ID de la position est obligatoire")
    private Long positionId;

    private String coverLetter;

    private BigDecimal aiMatchScore;

}