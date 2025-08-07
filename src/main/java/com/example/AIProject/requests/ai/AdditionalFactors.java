package com.example.AIProject.requests.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalFactors {
    private int experienceYears;
    private String location;
    private String[] skills;
    private double experienceWeight;
    private double locationWeight;
    private double skillsWeight;
}