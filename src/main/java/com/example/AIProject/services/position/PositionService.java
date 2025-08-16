package com.example.AIProject.services.position;

import com.example.AIProject.dto.PositionDto;
import com.example.AIProject.entities.Branch;
import com.example.AIProject.entities.Position;
import com.example.AIProject.enums.MobilityType;
import com.example.AIProject.enums.PositionStatus;
import com.example.AIProject.exceptions.ResourceNotFoundException;
import com.example.AIProject.exceptions.UnAuthorizedException;
import com.example.AIProject.repository.BranchRepository;
import com.example.AIProject.repository.PositionRepository;
import com.example.AIProject.requests.position.CreatePositionRequest;
import com.example.AIProject.requests.position.UpdatePositionRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PositionService implements IPositionService {

    private final PositionRepository positionRepository;
    private final BranchRepository branchRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PositionDto> getAllPositions() {
        return positionRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PositionDto> getPositionById(Long id) {
        return positionRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public PositionDto createPosition(CreatePositionRequest request) {
        Branch targetBranch = branchRepository.findById(request.getTargetBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branche introuvable avec l'ID: " + request.getTargetBranchId()));

        // Validation de la cohérence mobilité/branche
        if (request.getMobilityType() == MobilityType.INTERNAL &&
                !"Tunisia".equalsIgnoreCase(targetBranch.getCountry())) {
            throw new UnAuthorizedException("La mobilité interne doit être en Tunisie");
        }

        if (request.getMobilityType() == MobilityType.EXTERNAL &&
                "Tunisia".equalsIgnoreCase(targetBranch.getCountry())) {
            throw new UnAuthorizedException("La mobilité externe ne peut pas être en Tunisie");
        }

        Position position = new Position();
        position.setTitle(request.getTitle());
        position.setDepartment(request.getDepartment());
        position.setDescription(request.getDescription());
        position.setRequirements(request.getRequirements());
        position.setSalaryMin(request.getSalaryMin());
        position.setSalaryMax(request.getSalaryMax());
        position.setContractType(request.getContractType());
        position.setExperienceRequired(request.getExperienceRequired());
        position.setEducationRequired(request.getEducationRequired());
        position.setMobilityType(request.getMobilityType());
        position.setTargetBranch(targetBranch);

        // Pour mobilité externe, ajouter les contacts HR
        if (request.getMobilityType() == MobilityType.EXTERNAL) {
            position.setExternalHrContact(request.getExternalHrContact());
            position.setExternalHrEmail(request.getExternalHrEmail());
        }

        Position savedPosition = positionRepository.save(position);
        return convertToDto(savedPosition);
    }

    @Override
    public PositionDto updatePosition(Long id, UpdatePositionRequest request) {
        return positionRepository.findById(id)
                .map(existingPosition -> {
                    if (request.getTitle() != null) {
                        existingPosition.setTitle(request.getTitle());
                    }
                    if (request.getDepartment() != null) {
                        existingPosition.setDepartment(request.getDepartment());
                    }
                    if (request.getDescription() != null) {
                        existingPosition.setDescription(request.getDescription());
                    }
                    if (request.getRequirements() != null) {
                        existingPosition.setRequirements(request.getRequirements());
                    }
                    if (request.getSalaryMin() != null) {
                        existingPosition.setSalaryMin(request.getSalaryMin());
                    }
                    if (request.getSalaryMax() != null) {
                        existingPosition.setSalaryMax(request.getSalaryMax());
                    }
                    if (request.getContractType() != null) {
                        existingPosition.setContractType(request.getContractType());
                    }
                    if (request.getExperienceRequired() != null) {
                        existingPosition.setExperienceRequired(request.getExperienceRequired());
                    }
                    if (request.getEducationRequired() != null) {
                        existingPosition.setEducationRequired(request.getEducationRequired());
                    }

                    // Mise à jour du type de mobilité
                    if (request.getMobilityType() != null) {
                        existingPosition.setMobilityType(request.getMobilityType());
                    }

                    // Mise à jour de la branche cible
                    if (request.getTargetBranchId() != null) {
                        Branch targetBranch = branchRepository.findById(request.getTargetBranchId())
                                .orElseThrow(() -> new RuntimeException("Branche introuvable avec l'ID: " + request.getTargetBranchId()));
                        existingPosition.setTargetBranch(targetBranch);
                    }

                    // Mise à jour des contacts HR externes
                    if (request.getExternalHrContact() != null) {
                        existingPosition.setExternalHrContact(request.getExternalHrContact());
                    }
                    if (request.getExternalHrEmail() != null) {
                        existingPosition.setExternalHrEmail(request.getExternalHrEmail());
                    }

                    Position savedPosition = positionRepository.save(existingPosition);
                    return convertToDto(savedPosition);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Position not found with id: " + id));
    }

    @Override
    public void deletePosition(Long id) {
        if (!positionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Position not found with id: " + id);
        }
        positionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PositionDto> getPositionsByDepartment(String department) {
        return positionRepository.findByDepartment(department).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PositionDto> getActivePositions() {
        return positionRepository.findByStatus(PositionStatus.ACTIVE).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PositionDto> getPositionsByLocation(String country, String city) {
        return positionRepository.findByCountryAndCity(country, city).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PositionDto changePositionStatus(Long id, boolean isActive) {
        return positionRepository.findById(id)
                .map(position -> {
                    position.setStatus(isActive ? PositionStatus.ACTIVE : PositionStatus.INACTIVE);
                    Position savedPosition = positionRepository.save(position);
                    return convertToDto(savedPosition);
                })
                .orElseThrow(() -> new RuntimeException("Position not found with id: " + id));
    }

    private PositionDto convertToDto(Position position) {
        PositionDto dto = modelMapper.map(position, PositionDto.class);

        // Mapping manuel pour les champs de la branche
        if (position.getTargetBranch() != null) {
            dto.setTargetBranchId(position.getTargetBranch().getId());
            dto.setTargetBranchName(position.getTargetBranch().getBranchName());
            dto.setCountry(position.getTargetBranch().getCountry());
            dto.setCity(position.getTargetBranch().getCity());
        }

        dto.setApplicationsCount(position.getApplications() != null ? position.getApplications().size() : 0);

        return dto;
    }
}