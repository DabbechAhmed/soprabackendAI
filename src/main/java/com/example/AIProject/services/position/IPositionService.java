package com.example.AIProject.services.position;

import com.example.AIProject.dto.PositionDto;
import com.example.AIProject.requests.position.CreatePositionRequest;
import com.example.AIProject.requests.position.UpdatePositionRequest;

import java.util.List;
import java.util.Optional;

public interface IPositionService {
    List<PositionDto> getAllPositions();

    Optional<PositionDto> getPositionById(Long id);

    PositionDto createPosition(CreatePositionRequest request);

    PositionDto updatePosition(Long id, UpdatePositionRequest request);

    void deletePosition(Long id);

    List<PositionDto> getActivePositions();

    List<PositionDto> getPositionsByLocation(String country, String city);

    PositionDto changePositionStatus(Long id, boolean isActive);

    List<PositionDto> getPositionsByDepartment(String department);
}