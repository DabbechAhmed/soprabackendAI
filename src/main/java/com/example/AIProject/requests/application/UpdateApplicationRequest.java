package com.example.AIProject.requests.application;

import com.example.AIProject.enums.ApplicationStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
public class UpdateApplicationRequest {
    private String coverLetter;
    private ApplicationStatus status;
    private BigDecimal aiMatchScore;
}