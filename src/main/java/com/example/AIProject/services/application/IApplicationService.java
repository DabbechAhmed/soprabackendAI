package com.example.AIProject.services.application;

import com.example.AIProject.entities.Application;
import com.example.AIProject.enums.ApplicationStatus;
import com.example.AIProject.requests.application.CreateApplicationRequest;
import com.example.AIProject.requests.application.UpdateApplicationRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IApplicationService {
    List<Application> getAllApplications();
    Optional<Application> getApplicationById(Long id);
    Application createApplication(CreateApplicationRequest request);
    Application updateApplication(Long id, UpdateApplicationRequest request);
    void deleteApplication(Long id);
    List<Application> getApplicationsByUserId(Long userId);
    List<Application> getApplicationsByPositionId(Long positionId);
    List<Application> getApplicationsByStatus(ApplicationStatus status);
    Application updateApplicationStatus(Long id, ApplicationStatus status);
    Application updateAiMatchScore(Long id, BigDecimal score);
    boolean hasUserAppliedToPosition(Long userId, Long positionId);
}