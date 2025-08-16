package com.example.AIProject.services.application;

import com.example.AIProject.dto.ApplicationDto;
import com.example.AIProject.entities.Application;
import com.example.AIProject.entities.Position;
import com.example.AIProject.entities.User;
import com.example.AIProject.enums.ApplicationStatus;
import com.example.AIProject.enums.PositionStatus;
import com.example.AIProject.exceptions.ResourceNotFoundException;
import com.example.AIProject.exceptions.UnAuthorizedException;
import com.example.AIProject.repository.ApplicationRepository;
import com.example.AIProject.repository.PositionRepository;
import com.example.AIProject.repository.UserRepository;
import com.example.AIProject.requests.application.CreateApplicationRequest;
import com.example.AIProject.requests.application.UpdateApplicationRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationService implements IApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final PositionRepository positionRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationDto> getAllApplications() {
        return applicationRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ApplicationDto> getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .map(this::convertToDto);
    }

   @Override
   public ApplicationDto createApplication(CreateApplicationRequest request) {
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
       application.setAiMatchScore(request.getAiMatchScore()); // Utiliser le score de la requête

       Application savedApplication = applicationRepository.save(application);
       return convertToDto(savedApplication);
   }

    @Override
    public ApplicationDto updateApplication(Long id, UpdateApplicationRequest request) {
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

                    Application savedApplication = applicationRepository.save(existingApplication);
                    return convertToDto(savedApplication);
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
    public List<ApplicationDto> getApplicationsByUserId(Long userId) {
        return applicationRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationDto> getApplicationsByPositionId(Long positionId) {
        return applicationRepository.findByPositionId(positionId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationDto> getApplicationsByStatus(ApplicationStatus status) {
        return applicationRepository.findByStatus(status)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

  @Override
  public ApplicationDto acceptApplication(Long id) {
      return applicationRepository.findById(id)
              .map(application -> {
                  // Vérifier que la position est toujours active
                  if (application.getPosition().getStatus() != PositionStatus.ACTIVE) {
                      throw new UnAuthorizedException("La position n'est plus active");
                  }

                  application.setStatus(ApplicationStatus.ACCEPTED);

                  // Optionnel : Fermer automatiquement la position
                  Position position = application.getPosition();
                  position.setStatus(PositionStatus.FILLED);
                  positionRepository.save(position);

                  // Optionnel : Rejeter automatiquement les autres candidatures
                  rejectOtherApplicationsForPosition(application.getPosition().getId(), id);

                  Application savedApplication = applicationRepository.save(application);
                  return convertToDto(savedApplication);
              })
              .orElseThrow(() -> new ResourceNotFoundException("Candidature introuvable"));
  }
  @Override
  public ApplicationDto rejectApplication(Long id) {
      return applicationRepository.findById(id)
              .map(application -> {
                  // Vérifier que la candidature est encore en attente
                  if (application.getStatus() != ApplicationStatus.PENDING) {
                      throw new UnAuthorizedException("Cette candidature a déjà été traitée");
                  }

                  application.setStatus(ApplicationStatus.REJECTED);
                  Application savedApplication = applicationRepository.save(application);
                  return convertToDto(savedApplication);
              })
              .orElseThrow(() -> new ResourceNotFoundException("Candidature introuvable"));
  }

  private void rejectOtherApplicationsForPosition(Long positionId, Long acceptedApplicationId) {
      List<Application> otherApplications = applicationRepository
              .findByPositionIdAndStatusAndIdNot(positionId, ApplicationStatus.PENDING, acceptedApplicationId);

      otherApplications.forEach(app -> app.setStatus(ApplicationStatus.REJECTED));
      applicationRepository.saveAll(otherApplications);
  }
    @Override
    public ApplicationDto updateAiMatchScore(Long id, BigDecimal score) {
        return applicationRepository.findById(id)
                .map(application -> {
                    application.setAiMatchScore(score);
                    Application savedApplication = applicationRepository.save(application);
                    return convertToDto(savedApplication);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Candidature introuvable avec l'ID: " + id));
    }

    private ApplicationDto convertToDto(Application application) {
        return modelMapper.map(application, ApplicationDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserAppliedToPosition(Long userId, Long positionId) {
        return applicationRepository.findByUserIdAndPositionId(userId, positionId).isPresent();
    }
}