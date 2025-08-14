package com.example.AIProject.dto;

import com.example.AIProject.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDto {
    private Long id;
    private Long userId;
    private String userFullName;
    private String userEmail;
    private Long positionId;
    private String positionTitle;
    private String coverLetter;
    private ApplicationStatus status;
    private BigDecimal aiMatchScore;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
}