package com.example.AIProject.controllers;

import com.example.AIProject.dto.MatchScoreDto;
import com.example.AIProject.dto.ServiceMetrics;
import com.example.AIProject.requests.ai.SimilarityRequest;
import com.example.AIProject.services.ai.IAiRecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiRecommendationController {

    private final IAiRecommendationService aiRecommendationService;

    @PostMapping("/similarity")
    public ResponseEntity<MatchScoreDto> calculateSimilarity(
            @Valid @RequestBody SimilarityRequest request) {

        log.info("Calcul de similarité demandé");

        MatchScoreDto result = aiRecommendationService.calculateMatchScore(
                request.getCvText(), request.getJobText());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/similarity/enhanced")
    public ResponseEntity<MatchScoreDto> calculateEnhancedSimilarity(
            @Valid @RequestBody SimilarityRequest request) {

        MatchScoreDto result = aiRecommendationService.calculateEnhancedScore(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/similarity/async")
    public CompletableFuture<ResponseEntity<MatchScoreDto>> calculateSimilarityAsync(
            @Valid @RequestBody SimilarityRequest request) {

        return aiRecommendationService.calculateMatchScoreAsync(
                        request.getCvText(), request.getJobText())
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<MatchScoreDto>> calculateBatchSimilarity(
            @RequestParam String cvText,
            @RequestBody List<String> jobDescriptions) {

        List<MatchScoreDto> results = aiRecommendationService
                .calculateBatchMatchScores(cvText, jobDescriptions);

        return ResponseEntity.ok(results);
    }

    @GetMapping("/health")
    public ResponseEntity<String> checkHealth() {
        boolean isHealthy = aiRecommendationService.isServiceHealthy();
        return ResponseEntity.ok(isHealthy ? "UP" : "DOWN");
    }

    @GetMapping("/metrics")
    public ResponseEntity<ServiceMetrics> getMetrics() {
        return ResponseEntity.ok(aiRecommendationService.getServiceMetrics());
    }
}