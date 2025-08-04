package com.example.AIProject.Controllers;

import com.example.AIProject.entities.Position;
import com.example.AIProject.requests.position.CreatePositionRequest;
import com.example.AIProject.requests.position.UpdatePositionRequest;
import com.example.AIProject.responses.ApiResponse;
import com.example.AIProject.services.position.IPositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("${api.prefix}/positions")
@RequiredArgsConstructor
public class PositionController {

    private final IPositionService positionService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllPositions() {
        List<Position> positions = positionService.getAllPositions();
        return ResponseEntity.ok(new ApiResponse("Positions récupérées avec succès", positions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getPositionById(@PathVariable Long id) {
        return positionService.getPositionById(id)
                .map(position -> ResponseEntity.ok(new ApiResponse("Position récupérée avec succès", position)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("Position introuvable avec l'ID: " + id, null)));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse> getActivePositions() {
        List<Position> positions = positionService.getActivePositions();
        return ResponseEntity.ok(new ApiResponse("Positions actives récupérées avec succès", positions));
    }

    @GetMapping("/location")
    public ResponseEntity<ApiResponse> getPositionsByLocation(
            @RequestParam String country,
            @RequestParam String city) {
        List<Position> positions = positionService.getPositionsByLocation(country, city);
        return ResponseEntity.ok(new ApiResponse("Positions récupérées avec succès pour la localisation", positions));
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<ApiResponse> getPositionsByDepartment(@PathVariable String department) {
        List<Position> positions = positionService.getPositionsByDepartment(department);
        return ResponseEntity.ok(new ApiResponse("Positions récupérées avec succès pour le département", positions));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createPosition(@Valid @RequestBody CreatePositionRequest request) {
        try {
            Position createdPosition = positionService.createPosition(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Position créée avec succès", createdPosition));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updatePosition(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePositionRequest request) {
        try {
            Position updatedPosition = positionService.updatePosition(id, request);
            return ResponseEntity.ok(new ApiResponse("Position mise à jour avec succès", updatedPosition));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletePosition(@PathVariable Long id) {
        try {
            positionService.deletePosition(id);
            return ResponseEntity.ok(new ApiResponse("Position supprimée avec succès", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse> changePositionStatus(
            @PathVariable Long id,
            @RequestParam boolean isActive) {
        try {
            Position position = positionService.changePositionStatus(id, isActive);
            String statusMessage = isActive ? "activée" : "désactivée";
            return ResponseEntity.ok(new ApiResponse("Position " + statusMessage + " avec succès", position));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }
}