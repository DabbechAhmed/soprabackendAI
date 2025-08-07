package com.example.AIProject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PositionRecommendationDto {
    private Long positionId;
    private String positionTitle;
    private String department;
    private String targetBranch;
    private String country;
    private String city;
    private double matchScore;
    private String matchReason;
    private String contractType;
    private Integer experienceRequired;
    private String salaryRange;
}