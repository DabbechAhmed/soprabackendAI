package com.example.AIProject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchScoreDto {
    private double score;
    private long processingTime;
    private String status;
    private String mode; // "ai" ou "fallback"
    private String candidateId;
    private String jobId;
}