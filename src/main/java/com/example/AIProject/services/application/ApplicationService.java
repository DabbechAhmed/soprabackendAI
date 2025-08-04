package com.example.AIProject.services.application;

import com.example.AIProject.entities.Application;
import com.example.AIProject.entities.Position;
import com.example.AIProject.entities.User;
import com.example.AIProject.enums.ApplicationStatus;
import com.example.AIProject.exceptions.ResourceNotFoundException;
import com.example.AIProject.exceptions.UnAuthorizedException;
import com.example.AIProject.repository.ApplicationRepository;
import com.example.AIProject.repository.PositionRepository;
import com.example.AIProject.repository.UserRepository;
import com.example.AIProject.requests.application.CreateApplicationRequest;
import com.example.AIProject.requests.application.UpdateApplicationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationService implements IApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final PositionRepository positionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Application> getApplicationById(Long id) {
        return applicationRepository.findById(id);
    }

    @Override
    public Application createApplication(CreateApplicationRequest request) {
        // Vérifier si l'utilisateur a déjà postulé pour cette position
        if (hasUserAppliedToPosition(request.getUserId(), request.getPositionId())) {
            throw new UnAuthorizedException("L'utilisateur a déjà postulé pour cette position");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'ID: " + request.getUserId()));

        Position position = positionRepository.findById(request.getPositionId())
                .orElseThrow(() -> new ResourceNotFoundException("Position introuvable avec l'ID: " + request.getPositionId()));

        Application application = new Application();
        application.setUser(user);
        application.setPosition(position);
        application.setCoverLetter(request.getCoverLetter());
        application.setStatus(ApplicationStatus.PENDING);

        return applicationRepository.save(application);
    }

    @Override
    public Application updateApplication(Long id, UpdateApplicationRequest request) {
        return applicationRepository.findById(id)
                .map(existingApplication -> {
                    if (request.getCoverLetter() != null) {
                        existingApplication.setCoverLetter(request.getCoverLetter());
                    }
                    if (request.getStatus() != null) {
                        existingApplication.setStatus(request.getStatus());
                    }
                    if (request.getAiMatchScore() != null) {
                        existingApplication.setAiMatchScore(request.getAiMatchScore());
                    }

                    return applicationRepository.save(existingApplication);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Candidature introuvable avec l'ID: " + id));
    }

    @Override
    public void deleteApplication(Long id) {
        if (!applicationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Candidature introuvable avec l'ID: " + id);
        }
        applicationRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Application> getApplicationsByUserId(Long userId) {
        return applicationRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Application> getApplicationsByPositionId(Long positionId) {
        return applicationRepository.findByPositionId(positionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Application> getApplicationsByStatus(ApplicationStatus status) {
        return applicationRepository.findByStatus(status);
    }

    @Override
    public Application updateApplicationStatus(Long id, ApplicationStatus status) {
        return applicationRepository.findById(id)
                .map(application -> {
                    application.setStatus(status);
                    return applicationRepository.save(application);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Candidature introuvable avec l'ID: " + id));
    }

    @Override
    public Application updateAiMatchScore(Long id, BigDecimal score) {
        return applicationRepository.findById(id)
                .map(application -> {
                    application.setAiMatchScore(score);
                    return applicationRepository.save(application);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Candidature introuvable avec l'ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserAppliedToPosition(Long userId, Long positionId) {
        return applicationRepository.findByUserIdAndPositionId(userId, positionId).isPresent();
    }
}