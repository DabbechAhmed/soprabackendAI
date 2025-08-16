package com.example.AIProject.services.application;

import com.example.AIProject.dto.ApplicationDto;
import com.example.AIProject.entities.Application;
import com.example.AIProject.enums.ApplicationStatus;
import com.example.AIProject.requests.application.CreateApplicationRequest;
import com.example.AIProject.requests.application.UpdateApplicationRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IApplicationService {
    List<ApplicationDto> getAllApplications();
    Optional<ApplicationDto> getApplicationById(Long id);
    ApplicationDto createApplication(CreateApplicationRequest request);
    ApplicationDto updateApplication(Long id, UpdateApplicationRequest request);
    void deleteApplication(Long id);
    List<ApplicationDto> getApplicationsByUserId(Long userId);
    List<ApplicationDto> getApplicationsByPositionId(Long positionId);
    List<ApplicationDto> getApplicationsByStatus(ApplicationStatus status);
    ApplicationDto acceptApplication(Long id);

    ApplicationDto rejectApplication(Long id);

    ApplicationDto updateAiMatchScore(Long id, BigDecimal score);
    boolean hasUserAppliedToPosition(Long userId, Long positionId);
}