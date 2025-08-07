package com.example.AIProject.services.ai;

import com.example.AIProject.dto.*;
import com.example.AIProject.requests.ai.AdditionalFactors;
import com.example.AIProject.requests.ai.SimilarityRequest;
import com.example.AIProject.responses.SimilarityResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service

public class AiRecommendationService implements IAiRecommendationService {


    @Value("${ai.service.url:http://localhost:8000}")
    private String aiServiceUrl;

    @Value("${ai.service.timeout:5000}")
    private int timeoutMs;

    @Value("${ai.service.fallback.enabled:true}")
    private boolean fallbackEnabled;

    @Value("${ai.service.similarity.threshold:0.3}")
    private double similarityThreshold;

    private final RestTemplate restTemplate;
    private final Executor aiTaskExecutor;
    private final Map<String, Object> metricsCache = new ConcurrentHashMap<>();
    private volatile boolean serviceHealthy = true;

    public AiRecommendationService(RestTemplate restTemplate,
                                   @Qualifier("aiTaskExecutor") Executor aiTaskExecutor) {
        this.restTemplate = restTemplate;
        this.aiTaskExecutor = aiTaskExecutor;
        initializeMetrics();
    }


    @PostConstruct
    public void initialize() {
        log.info("Initialisation du service AI avec l'URL: {}", aiServiceUrl);
        checkServiceHealth();
    }

    private void initializeMetrics() {
        metricsCache.put("totalRequests", 0L);
        metricsCache.put("totalResponseTime", 0L);
        metricsCache.put("errorCount", 0L);
        metricsCache.put("lastHealthCheck", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

   @Override
    public MatchScoreDto calculateMatchScore(String cvText, String jobText) {
        log.debug("Calcul du score de similarité pour CV ({}chars) et Job ({}chars)",
                 cvText.length(), jobText.length());

        if (isTextEmpty(cvText) || isTextEmpty(jobText)) {
            return createErrorResponse("Texte CV ou Job vide", "error");
        }

        try {
            long startTime = System.currentTimeMillis();

            SimilarityRequest request = SimilarityRequest.builder()
                    .cvText(truncateText(cvText))
                    .jobText(truncateText(jobText))
                    .build();

            // LOG AJOUTÉ : Voir ce qui est envoyé
            log.info("Requête envoyée au service AI: cvText={}, jobText={}",
                     request.getCvText(), request.getJobText());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<SimilarityRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<SimilarityResponse> response = restTemplate.postForEntity(
                    aiServiceUrl + "/api/v1/similarity",
                    entity,
                    SimilarityResponse.class
            );

            // LOG AJOUTÉ : Voir ce qui est reçu
            log.info("Réponse reçue du service AI: {}", response.getBody());

            long processingTime = System.currentTimeMillis() - startTime;
            updateMetrics(processingTime, true);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                SimilarityResponse body = response.getBody();
                return MatchScoreDto.builder()
                        .score(body.getSimilarityScore())
                        .processingTime(processingTime)
                        .status(body.getStatus())
                        .mode("ai")
                        .build();
            } else {
                log.warn("Service AI a retourné un code d'erreur: {}", response.getStatusCode());
                return handleFallback(cvText, jobText, "Service AI indisponible");
            }

        } catch (Exception e) {
            log.error("Erreur lors de l'appel au service AI", e);
            updateMetrics(0, false);
            return handleFallback(cvText, jobText, "Erreur du service AI: " + e.getMessage());
        }
    }

    @Override
    public List<MatchScoreDto> calculateBatchMatchScores(String cvText, List<String> jobDescriptions) {
        log.debug("Calcul batch pour {} descriptions de poste", jobDescriptions.size());

        return jobDescriptions.parallelStream()
                .map(jobText -> calculateMatchScore(cvText, jobText))
                .collect(Collectors.toList());
    }


    @Override
    @Async("aiTaskExecutor")
    public CompletableFuture<MatchScoreDto> calculateMatchScoreAsync(String cvText, String jobText) {
        try {
            MatchScoreDto result = calculateMatchScore(cvText, jobText);
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("Erreur lors du calcul asynchrone", e);
            return CompletableFuture.completedFuture(
                    createErrorResponse("Erreur async: " + e.getMessage(), "error")
            );
        }
    }

    @Override
    public List<MatchScoreDto> findTopCandidates(String jobText, List<String> candidateCVs, int topN) {
        log.debug("Recherche des {} meilleurs candidats parmi {}", topN, candidateCVs.size());

        return IntStream.range(0, candidateCVs.size())
                .parallel()
                .mapToObj(i -> {
                    MatchScoreDto score = calculateMatchScore(candidateCVs.get(i), jobText);
                    score.setCandidateId("candidate_" + i);
                    return score;
                })
                .filter(score -> score.getScore() >= similarityThreshold * 100)
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(topN)
                .collect(Collectors.toList());
    }

    @Override
    public List<MatchScoreDto> findMatchingJobs(String cvText, List<String> jobDescriptions, double minScore) {
        log.debug("Recherche des postes correspondants avec score minimum: {}", minScore);

        return IntStream.range(0, jobDescriptions.size())
                .parallel()
                .mapToObj(i -> {
                    MatchScoreDto score = calculateMatchScore(cvText, jobDescriptions.get(i));
                    score.setJobId("job_" + i);
                    return score;
                })
                .filter(score -> score.getScore() >= minScore)
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .collect(Collectors.toList());
    }

    @Override
    public MatchScoreDto calculateEnhancedScore(SimilarityRequest request) {
        MatchScoreDto baseScore = calculateMatchScore(request.getCvText(), request.getJobText());

        if (request.getAdditionalFactors() != null) {
            double enhancedScore = calculateEnhancedScoreWithFactors(baseScore, request.getAdditionalFactors());
            baseScore.setScore(enhancedScore);
            baseScore.setStatus("enhanced");
        }

        return baseScore;
    }

    @Override
    public boolean isServiceHealthy() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    aiServiceUrl + "/health", Map.class);

            serviceHealthy = response.getStatusCode().is2xxSuccessful();
            updateHealthCheckTime();
            return serviceHealthy;

        } catch (Exception e) {
            log.warn("Service AI non disponible lors du health check", e);
            serviceHealthy = false;
            return false;
        }
    }

    @Override
    public ServiceMetrics getServiceMetrics() {
        long totalRequests = (Long) metricsCache.get("totalRequests");
        long totalResponseTime = (Long) metricsCache.get("totalResponseTime");
        long errorCount = (Long) metricsCache.get("errorCount");

        double averageResponseTime = totalRequests > 0 ?
                (double) totalResponseTime / totalRequests : 0;
        double errorRate = totalRequests > 0 ?
                (double) errorCount / totalRequests : 0;

        return ServiceMetrics.builder()
                .totalRequests(totalRequests)
                .averageResponseTime(averageResponseTime)
                .errorRate(errorRate)
                .isHealthy(serviceHealthy)
                .lastHealthCheck((String) metricsCache.get("lastHealthCheck"))
                .build();
    }

    @Override
    public MatchScoreDto calculateWithFallback(String cvText, String jobText) {
        MatchScoreDto result = calculateMatchScore(cvText, jobText);

        if ("fallback".equals(result.getMode())) {
            log.info("Utilisation du mode fallback pour le calcul de similarité");
        }

        return result;
    }

    @Override
    public void warmupService(List<String> commonTexts) {
        log.info("Préchauffage du service AI avec {} textes", commonTexts.size());

        if (commonTexts.size() >= 2) {
            // Test avec les premiers textes pour préchauffer le modèle
            calculateMatchScore(commonTexts.get(0), commonTexts.get(1));
        }
    }

    private MatchScoreDto handleFallback(String cvText, String jobText, String reason) {
        if (!fallbackEnabled) {
            return createErrorResponse(reason, "error");
        }

        log.info("Utilisation du fallback: {}", reason);
        double fallbackScore = calculateKeywordSimilarity(cvText, jobText);

        return MatchScoreDto.builder()
                .score(fallbackScore)
                .processingTime(0L)
                .status("fallback")
                .mode("fallback")
                .build();
    }

    private double calculateKeywordSimilarity(String text1, String text2) {
        if (isTextEmpty(text1) || isTextEmpty(text2)) return 0.0;

        Set<String> words1 = extractKeywords(text1.toLowerCase());
        Set<String> words2 = extractKeywords(text2.toLowerCase());

        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);

        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size() * 100;
    }

    private Set<String> extractKeywords(String text) {
        // Mots-clés techniques et métiers courants
        Set<String> technicalKeywords = Set.of(
                "java", "python", "javascript", "react", "spring", "docker",
                "kubernetes", "aws", "azure", "sql", "mongodb", "machine learning",
                "ai", "artificial intelligence", "data science", "frontend", "backend"
        );

        return Arrays.stream(text.split("\\W+"))
                .filter(word -> word.length() > 2)
                .filter(word -> technicalKeywords.contains(word) || word.length() > 4)
                .collect(Collectors.toSet());
    }

    private double calculateEnhancedScoreWithFactors(MatchScoreDto baseScore, AdditionalFactors factors) {
        double base = baseScore.getScore() * 0.6;
        double experienceBonus = calculateExperienceBonus(factors.getExperienceYears()) *
                                factors.getExperienceWeight();
        double locationBonus = calculateLocationBonus(factors.getLocation()) *
                              factors.getLocationWeight();
        double skillsBonus = calculateSkillsBonus(factors.getSkills()) *
                            factors.getSkillsWeight();

        return Math.min(base + experienceBonus + locationBonus + skillsBonus, 100.0);
    }

    private double calculateExperienceBonus(int years) {
        return Math.min(years * 2.0, 20.0); // Max 20 points
    }

    private double calculateLocationBonus(String location) {
        // Bonus simple basé sur la localisation préférée
        return location != null && !location.trim().isEmpty() ? 5.0 : 0.0;
    }

    private double calculateSkillsBonus(String[] skills) {
        // Bonus basé sur le nombre de compétences
        return skills != null ? Math.min(skills.length * 1.5, 15.0) : 0.0;
    }

    private void updateMetrics(long responseTime, boolean success) {
        synchronized (metricsCache) {
            metricsCache.put("totalRequests", (Long) metricsCache.get("totalRequests") + 1);
            metricsCache.put("totalResponseTime", (Long) metricsCache.get("totalResponseTime") + responseTime);

            if (!success) {
                metricsCache.put("errorCount", (Long) metricsCache.get("errorCount") + 1);
            }
        }
    }

    private void updateHealthCheckTime() {
        metricsCache.put("lastHealthCheck", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    private void checkServiceHealth() {
        try {
            isServiceHealthy();
            log.info("Service AI health check: {}", serviceHealthy ? "OK" : "FAILED");
        } catch (Exception e) {
            log.warn("Impossible de vérifier la santé du service AI au démarrage", e);
        }
    }

    private boolean isTextEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    private String truncateText(String text) {
        int maxLength = 5000; // Limite configurée
        return text.length() > maxLength ? text.substring(0, maxLength) : text;
    }

    private MatchScoreDto createErrorResponse(String message, String status) {
        log.warn("Création d'une réponse d'erreur: {}", message);
        return MatchScoreDto.builder()
                .score(0.0)
                .processingTime(0L)
                .status(status)
                .mode("error")
                .build();
    }
}