package com.example.AIProject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceMetrics {
    private long totalRequests;
    private double averageResponseTime;
    private double errorRate;
    private boolean isHealthy;
    private String lastHealthCheck;
}