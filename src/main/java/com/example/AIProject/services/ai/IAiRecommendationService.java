package com.example.AIProject.services.ai;

import com.example.AIProject.dto.MatchScoreDto;
import com.example.AIProject.dto.ServiceMetrics;
import com.example.AIProject.requests.ai.SimilarityRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Interface pour les services de recommandation AI
 * Intégration avec le microservice FastAPI pour le matching CV-job
 */
public interface IAiRecommendationService {

    /**
     * Calcule le score de similarité entre un CV et une description de poste
     * @param cvText Le texte du CV
     * @param jobText La description du poste
     * @return MatchScoreDto contenant le score et les métadonnées
     */
    MatchScoreDto calculateMatchScore(String cvText, String jobText);

    /**
     * Calcule les scores de similarité pour plusieurs postes en lot
     * @param cvText Le texte du CV
     * @param jobDescriptions Liste des descriptions de postes
     * @return Liste des scores correspondants
     */
    List<MatchScoreDto> calculateBatchMatchScores(String cvText, List<String> jobDescriptions);

    /**
     * Version asynchrone du calcul de similarité
     * @param cvText Le texte du CV
     * @param jobText La description du poste
     * @return CompletableFuture<MatchScoreDto>
     */
    CompletableFuture<MatchScoreDto> calculateMatchScoreAsync(String cvText, String jobText);

    /**
     * Trouve les meilleurs candidats pour un poste donné
     * @param jobText La description du poste
     * @param candidateCVs Liste des CVs candidats
     * @param topN Nombre de meilleurs candidats à retourner
     * @return Liste triée des meilleurs matches
     */
    List<MatchScoreDto> findTopCandidates(String jobText, List<String> candidateCVs, int topN);

    /**
     * Trouve les meilleurs postes pour un CV donné
     * @param cvText Le texte du CV
     * @param jobDescriptions Liste des descriptions de postes
     * @param minScore Score minimum pour filtrer les résultats
     * @return Liste triée des meilleurs postes
     */
    List<MatchScoreDto> findMatchingJobs(String cvText, List<String> jobDescriptions, double minScore);

    /**
     * Calcul avec facteurs additionnels (expérience, compétences, localisation)
     * @param request Requête contenant CV, job et facteurs additionnels
     * @return Score enrichi avec les facteurs
     */
    MatchScoreDto calculateEnhancedScore(SimilarityRequest request);

    /**
     * Vérifie la santé du service AI
     * @return true si le service est opérationnel
     */
    boolean isServiceHealthy();

    /**
     * Obtient les statistiques de performance du service
     * @return Métriques de performance
     */
    ServiceMetrics getServiceMetrics();

    /**
     * Calcul de similarité avec fallback en cas d'erreur du service AI
     * @param cvText Le texte du CV
     * @param jobText La description du poste
     * @return Score avec indication du mode utilisé (AI ou fallback)
     */
    MatchScoreDto calculateWithFallback(String cvText, String jobText);

    /**
     * Préchauffe le cache du service pour améliorer les performances
     * @param commonTexts Textes fréquemment utilisés pour préchauffage
     */
    void warmupService(List<String> commonTexts);
}