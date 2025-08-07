package com.example.AIProject.controllers;

import com.example.AIProject.entities.Application;
import com.example.AIProject.enums.ApplicationStatus;
import com.example.AIProject.exceptions.ResourceNotFoundException;
import com.example.AIProject.exceptions.UnAuthorizedException;
import com.example.AIProject.requests.application.CreateApplicationRequest;
import com.example.AIProject.requests.application.UpdateApplicationRequest;
import com.example.AIProject.responses.ApiResponse;
import com.example.AIProject.services.application.IApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("${api.prefix}/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final IApplicationService applicationService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllApplications() {
        List<Application> applications = applicationService.getAllApplications();
        return ResponseEntity.ok(new ApiResponse("Candidatures récupérées avec succès", applications));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getApplicationById(@PathVariable Long id) {
        return applicationService.getApplicationById(id)
                .map(application -> ResponseEntity.ok(new ApiResponse("Candidature récupérée avec succès", application)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("Candidature introuvable avec l'ID: " + id, null)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getApplicationsByUserId(@PathVariable Long userId) {
        List<Application> applications = applicationService.getApplicationsByUserId(userId);
        return ResponseEntity.ok(new ApiResponse("Candidatures récupérées avec succès pour l'utilisateur", applications));
    }

    @GetMapping("/position/{positionId}")
    public ResponseEntity<ApiResponse> getApplicationsByPositionId(@PathVariable Long positionId) {
        List<Application> applications = applicationService.getApplicationsByPositionId(positionId);
        return ResponseEntity.ok(new ApiResponse("Candidatures récupérées avec succès pour la position", applications));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse> getApplicationsByStatus(@PathVariable ApplicationStatus status) {
        List<Application> applications = applicationService.getApplicationsByStatus(status);
        return ResponseEntity.ok(new ApiResponse("Candidatures récupérées avec succès par statut", applications));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createApplication(@Valid @RequestBody CreateApplicationRequest request) {
        try {
            Application createdApplication = applicationService.createApplication(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Candidature créée avec succès", createdApplication));
        } catch (UnAuthorizedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateApplication(
            @PathVariable Long id,
            @Valid @RequestBody UpdateApplicationRequest request) {
        try {
            Application updatedApplication = applicationService.updateApplication(id, request);
            return ResponseEntity.ok(new ApiResponse("Candidature mise à jour avec succès", updatedApplication));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status) {
        try {
            Application application = applicationService.updateApplicationStatus(id, status);
            return ResponseEntity.ok(new ApiResponse("Statut de la candidature mis à jour avec succès", application));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/{id}/ai-score")
    public ResponseEntity<ApiResponse> updateAiMatchScore(
            @PathVariable Long id,
            @RequestParam BigDecimal score) {
        try {
            Application application = applicationService.updateAiMatchScore(id, score);
            return ResponseEntity.ok(new ApiResponse("Score IA mis à jour avec succès", application));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteApplication(@PathVariable Long id) {
        try {
            applicationService.deleteApplication(id);
            return ResponseEntity.ok(new ApiResponse("Candidature supprimée avec succès", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse> checkApplicationExists(
            @RequestParam Long userId,
            @RequestParam Long positionId) {
        boolean exists = applicationService.hasUserAppliedToPosition(userId, positionId);
        return ResponseEntity.ok(new ApiResponse("Vérification effectuée", exists));
    }
}