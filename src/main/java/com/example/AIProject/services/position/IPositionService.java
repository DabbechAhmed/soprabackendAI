package com.example.AIProject.services.position;

    import com.example.AIProject.entities.Position;
    import com.example.AIProject.requests.position.CreatePositionRequest;
    import com.example.AIProject.requests.position.UpdatePositionRequest;

    import java.util.List;
    import java.util.Optional;

    public interface IPositionService {
        List<Position> getAllPositions();
        Optional<Position> getPositionById(Long id);
        Position createPosition(CreatePositionRequest request);
        Position updatePosition(Long id, UpdatePositionRequest request);
        void deletePosition(Long id);
        List<Position> getActivePositions();
        List<Position> getPositionsByLocation(String country, String city);
        Position changePositionStatus(Long id, boolean isActive);
        List<Position> getPositionsByDepartment(String department);
    }