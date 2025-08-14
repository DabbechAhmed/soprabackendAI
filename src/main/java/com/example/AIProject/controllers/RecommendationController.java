package com.example.AIProject.controllers;

import com.example.AIProject.dto.PositionRecommendationDto;
import com.example.AIProject.services.ai.PositionRecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final PositionRecommendationService recommendationService;

    @GetMapping("/positions/{employeeId}")
    public ResponseEntity<List<PositionRecommendationDto>> getRecommendedPositions(
            @PathVariable Long employeeId) {

        log.info("Demande de recommandations pour l'employé: {}", employeeId);

        List<PositionRecommendationDto> recommendations =
                recommendationService.getRecommendedPositions(employeeId);

        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/positions/{employeeId}/country/{country}")
    public ResponseEntity<List<PositionRecommendationDto>> getRecommendedPositionsByCountry(
            @PathVariable Long employeeId,
            @PathVariable String country) {

        log.info("Demande de recommandations pour l'employé {} dans le pays {}",
                employeeId, country);

        List<PositionRecommendationDto> recommendations =
                recommendationService.getRecommendedPositionsByCountry(employeeId, country);

        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/positions/{employeeId}/country/{country}/city/{city}")
    public ResponseEntity<List<PositionRecommendationDto>> getRecommendedPositionsByCountryAndCity(
            @PathVariable Long employeeId,
            @PathVariable String country,
            @PathVariable String city) {

        log.info("Demande de recommandations pour l'employé {} dans {} - {}",
                employeeId, country, city);

        List<PositionRecommendationDto> recommendations =
                recommendationService.getRecommendedPositionsByCountryAndCity(employeeId, country, city);

        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/positions/{employeeId}/location")
    public ResponseEntity<List<PositionRecommendationDto>> getRecommendedPositionsByLocation(
            @PathVariable Long employeeId,
            @RequestParam String country,
            @RequestParam(required = false) String city) {

        log.info("Demande de recommandations pour l'employé {} - Pays: {}, Ville: {}",
                employeeId, country, city);

        List<PositionRecommendationDto> recommendations;

        if (city != null && !city.trim().isEmpty()) {
            recommendations = recommendationService.getRecommendedPositionsByCountryAndCity(
                    employeeId, country, city);
        } else {
            recommendations = recommendationService.getRecommendedPositionsByCountry(
                    employeeId, country);
        }

        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/positions/{employeeId}/branch/{branchId}")
    public ResponseEntity<List<PositionRecommendationDto>> getRecommendedPositionsByBranch(
            @PathVariable Long employeeId,
            @PathVariable Long branchId) {

        log.info("Demande de recommandations pour l'employé {} dans la branche {}",
                employeeId, branchId);

        List<PositionRecommendationDto> recommendations =
                recommendationService.getRecommendedPositionsByBranch(employeeId, branchId);

        return ResponseEntity.ok(recommendations);
    }
}