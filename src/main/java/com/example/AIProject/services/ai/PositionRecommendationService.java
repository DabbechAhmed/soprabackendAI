package com.example.AIProject.services.ai;

import com.example.AIProject.dto.PositionRecommendationDto;
import com.example.AIProject.dto.MatchScoreDto;
import com.example.AIProject.entities.EmployeeProfile;
import com.example.AIProject.entities.Position;
import com.example.AIProject.enums.PositionStatus;

import com.example.AIProject.repository.EmployeeProfileRepository;
import com.example.AIProject.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PositionRecommendationService {

    private final IAiRecommendationService aiRecommendationService;
    private final EmployeeProfileRepository employeeProfileRepository;
    private final PositionRepository positionRepository;

    @Value("${recommendation.min.score:60.0}")
    private double minRecommendationScore;

    @Value("${recommendation.max.results:10}")
    private int maxResults;

    public List<PositionRecommendationDto> getRecommendedPositions(Long employeeId) {
        log.info("Recherche de recommandations pour l'employé ID: {}", employeeId);

        // Récupérer le profil de l'employé
        EmployeeProfile profile = employeeProfileRepository.findByUserId(employeeId)
                .orElseThrow(() -> new RuntimeException("Profil employé non trouvé"));

        if (profile.getCvText() == null || profile.getCvText().trim().isEmpty()) {
            throw new RuntimeException("Le CV de l'employé est requis pour les recommandations");
        }

        // Récupérer toutes les positions actives
        List<Position> activePositions = positionRepository.findByStatus(PositionStatus.ACTIVE);

        log.info("Analyse de {} positions actives", activePositions.size());

        return activePositions.stream()
                .map(position -> calculatePositionMatch(profile, position))
                .filter(recommendation -> recommendation.getMatchScore() >= minRecommendationScore)
                .sorted((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()))
                .limit(maxResults)
                .collect(Collectors.toList());
    }

    public List<PositionRecommendationDto> getRecommendedPositionsByCountry(Long employeeId, String country) {
        log.info("Recherche de recommandations pour l'employé {} dans le pays {}", employeeId, country);

        EmployeeProfile profile = employeeProfileRepository.findByUserId(employeeId)
                .orElseThrow(() -> new RuntimeException("Profil employé non trouvé"));

        if (profile.getCvText() == null || profile.getCvText().trim().isEmpty()) {
            throw new RuntimeException("Le CV de l'employé est requis pour les recommandations");
        }

        List<Position> countryPositions = positionRepository.findActivePositionsByCountry(country);

        log.info("Analyse de {} positions dans {}", countryPositions.size(), country);

        return countryPositions.stream()
                .map(position -> calculatePositionMatch(profile, position))
                .filter(recommendation -> recommendation.getMatchScore() >= minRecommendationScore)
                .sorted((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()))
                .limit(maxResults)
                .collect(Collectors.toList());
    }

    public List<PositionRecommendationDto> getRecommendedPositionsByCountryAndCity(
            Long employeeId, String country, String city) {
        log.info("Recherche de recommandations pour l'employé {} dans {} - {}", employeeId, country, city);

        EmployeeProfile profile = employeeProfileRepository.findByUserId(employeeId)
                .orElseThrow(() -> new RuntimeException("Profil employé non trouvé"));

        if (profile.getCvText() == null || profile.getCvText().trim().isEmpty()) {
            throw new RuntimeException("Le CV de l'employé est requis pour les recommandations");
        }

        List<Position> locationPositions = positionRepository.findActivePositionsByCountryAndCity(country, city);

        log.info("Analyse de {} positions dans {} - {}", locationPositions.size(), country, city);

        return locationPositions.stream()
                .map(position -> calculatePositionMatch(profile, position))
                .filter(recommendation -> recommendation.getMatchScore() >= minRecommendationScore)
                .sorted((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()))
                .limit(maxResults)
                .collect(Collectors.toList());
    }

    public List<PositionRecommendationDto> getRecommendedPositionsByBranch(Long employeeId, Long branchId) {
        log.info("Recherche de recommandations pour l'employé {} dans la branche {}", employeeId, branchId);

        EmployeeProfile profile = employeeProfileRepository.findByUserId(employeeId)
                .orElseThrow(() -> new RuntimeException("Profil employé non trouvé"));

        List<Position> branchPositions = positionRepository.findByTargetBranchIdAndStatus(branchId, PositionStatus.ACTIVE);

        return branchPositions.stream()
                .map(position -> calculatePositionMatch(profile, position))
                .filter(recommendation -> recommendation.getMatchScore() >= minRecommendationScore)
                .sorted((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()))
                .limit(maxResults)
                .collect(Collectors.toList());
    }

    private PositionRecommendationDto calculatePositionMatch(EmployeeProfile profile, Position position) {
        // Calculer le score AI
        MatchScoreDto matchScore = aiRecommendationService.calculateMatchScore(
                profile.getCvText(),
                position.getJobText()
        );

        // Ajuster le score avec des facteurs additionnels
        double adjustedScore = adjustScoreWithFactors(matchScore.getScore(), profile, position);

        return PositionRecommendationDto.builder()
                .positionId(position.getId())
                .positionTitle(position.getTitle())
                .department(position.getDepartment())
                .targetBranch(position.getTargetBranch().getBranchName())
                .country(position.getCountry())
                .city(position.getCity())
                .matchScore(adjustedScore)
                .matchReason(generateMatchReason(adjustedScore, profile, position))
                .contractType(position.getContractType().toString())
                .experienceRequired(position.getExperienceRequired())
                .salaryRange(formatSalaryRange(position.getSalaryMin(), position.getSalaryMax()))
                .build();
    }

    private double adjustScoreWithFactors(double baseScore, EmployeeProfile profile, Position position) {
        double adjustedScore = baseScore;

        // Bonus pour l'expérience
        if (profile.getExperienceYears() >= position.getExperienceRequired()) {
            adjustedScore += 5.0; // +5 points si expérience suffisante
        } else if (profile.getExperienceYears() < position.getExperienceRequired() - 2) {
            adjustedScore -= 10.0; // -10 points si expérience insuffisante
        }

        // Bonus pour l'éducation
        if (profile.getEducation() != null && position.getEducationRequired() != null) {
            if (profile.getEducation().ordinal() >= position.getEducationRequired().ordinal()) {
                adjustedScore += 3.0; // +3 points si niveau d'éducation suffisant
            }
        }

        // Bonus pour la localisation
        if (position.getCountry() != null && position.getCountry().equalsIgnoreCase(profile.getCountry())) {
            adjustedScore += 2.0; // +2 points même pays
            if (position.getCity() != null && position.getCity().equalsIgnoreCase(profile.getCity())) {
                adjustedScore += 3.0; // +3 points même ville
            }
        }

        return Math.max(0, Math.min(100, adjustedScore)); // Garder entre 0 et 100
    }

    private String generateMatchReason(double score, EmployeeProfile profile, Position position) {
        StringBuilder reason = new StringBuilder();

        if (score >= 80) {
            reason.append("Excellente correspondance - ");
        } else if (score >= 70) {
            reason.append("Bonne correspondance - ");
        } else {
            reason.append("Correspondance partielle - ");
        }

        if (profile.getExperienceYears() >= position.getExperienceRequired()) {
            reason.append("Expérience suffisante. ");
        }

        if (position.getCountry() != null && position.getCountry().equalsIgnoreCase(profile.getCountry())) {
            reason.append("Même localisation. ");
        }

        return reason.toString().trim();
    }

    private String formatSalaryRange(BigDecimal min, BigDecimal max) {
        if (min == null && max == null) return "Non spécifié";
        if (min == null) return "Jusqu'à " + max + "€";
        if (max == null) return "À partir de " + min + "€";
        return min + "€ - " + max + "€";
    }
}