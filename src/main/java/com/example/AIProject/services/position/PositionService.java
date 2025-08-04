package com.example.AIProject.services.position;

import com.example.AIProject.entities.Branch;
import com.example.AIProject.entities.Position;
import com.example.AIProject.enums.MobilityType;
import com.example.AIProject.enums.PositionStatus;
import com.example.AIProject.repository.BranchRepository;
import com.example.AIProject.repository.PositionRepository;
import com.example.AIProject.requests.position.CreatePositionRequest;
import com.example.AIProject.requests.position.UpdatePositionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PositionService implements IPositionService {

    private final PositionRepository positionRepository;
    private final BranchRepository branchRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Position> getAllPositions() {
        return positionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Position> getPositionById(Long id) {
        return positionRepository.findById(id);
    }

  @Override
    public Position createPosition(CreatePositionRequest request) {
        Branch targetBranch = branchRepository.findById(request.getTargetBranchId())
                .orElseThrow(() -> new RuntimeException("Branche introuvable avec l'ID: " + request.getTargetBranchId()));

        // Validation de la cohérence mobilité/branche
        if (request.getMobilityType() == MobilityType.INTERNAL &&
            !"Tunisia".equalsIgnoreCase(targetBranch.getCountry())) {
            throw new IllegalArgumentException("La mobilité interne doit être en Tunisie");
        }

        if (request.getMobilityType() == MobilityType.EXTERNAL &&
            "Tunisia".equalsIgnoreCase(targetBranch.getCountry())) {
            throw new IllegalArgumentException("La mobilité externe ne peut pas être en Tunisie");
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

        return positionRepository.save(position);
    }

   @Override
    public Position updatePosition(Long id, UpdatePositionRequest request) {
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

                    return positionRepository.save(existingPosition);
                })
                .orElseThrow(() -> new RuntimeException("Position not found with id: " + id));
    }

    @Override
    public void deletePosition(Long id) {
        if (!positionRepository.existsById(id)) {
            throw new RuntimeException("Position not found with id: " + id);
        }
        positionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Position> getPositionsByDepartment(String department) {
        return positionRepository.findByDepartment(department);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Position> getActivePositions() {
        return positionRepository.findByStatus(PositionStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Position> getPositionsByLocation(String country, String city) {
        return positionRepository.findByCountryAndCity(country, city);
    }

    @Override
    public Position changePositionStatus(Long id, boolean isActive) {
        return positionRepository.findById(id)
                .map(position -> {
                    position.setStatus(isActive ? PositionStatus.ACTIVE : PositionStatus.INACTIVE);
                    return positionRepository.save(position);
                })
                .orElseThrow(() -> new RuntimeException("Position not found with id: " + id));
    }
}