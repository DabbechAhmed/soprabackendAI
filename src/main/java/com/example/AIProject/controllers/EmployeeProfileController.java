package com.example.AIProject.controllers;

import com.example.AIProject.dto.EmployeeProfileDTO;
import com.example.AIProject.entities.EmployeeProfile;
import com.example.AIProject.requests.employeeProfile.CreateEmployeeProfileRequest;
import com.example.AIProject.requests.employeeProfile.UpdateEmployeeProfileRequest;
import com.example.AIProject.responses.ApiResponse;
import com.example.AIProject.services.employeeProfile.IEmployeeProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("${api.prefix}/employee-profiles")
@RequiredArgsConstructor
public class EmployeeProfileController {

    private final IEmployeeProfileService employeeProfileService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllProfiles() {
        List<EmployeeProfile> profiles = employeeProfileService.getAllProfiles();
        List<EmployeeProfileDTO> profileDTOs = profiles.stream()
                .map(employeeProfileService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse("Profiles retrieved successfully", profileDTOs));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProfileById(@PathVariable Long id) {
        return employeeProfileService.getProfileById(id)
                .map(profile -> {
                    EmployeeProfileDTO profileDTO = employeeProfileService.convertToDTO(profile);
                    return ResponseEntity.ok(new ApiResponse("Profile retrieved successfully", profileDTO));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("Profile not found with id: " + id, null)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getProfileByUserId(@PathVariable Long userId) {
        return employeeProfileService.getProfileByUserId(userId)
                .map(profile -> {
                    EmployeeProfileDTO profileDTO = employeeProfileService.convertToDTO(profile);
                    return ResponseEntity.ok(new ApiResponse("Profile retrieved successfully", profileDTO));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("Profile not found for user ID: " + userId, null)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createProfile(@Valid @RequestBody CreateEmployeeProfileRequest request) {
        try {
            EmployeeProfile createdProfile = employeeProfileService.createProfile(request);
            EmployeeProfileDTO profileDTO = employeeProfileService.convertToDTO(createdProfile);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Profile created successfully", profileDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateProfile(@PathVariable Long id, @Valid @RequestBody UpdateEmployeeProfileRequest request) {
        try {
            EmployeeProfile updatedProfile = employeeProfileService.updateProfile(id, request);
            EmployeeProfileDTO profileDTO = employeeProfileService.convertToDTO(updatedProfile);
            return ResponseEntity.ok(new ApiResponse("Profile updated successfully", profileDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteProfile(@PathVariable Long id) {
        try {
            employeeProfileService.deleteProfile(id);
            return ResponseEntity.ok(new ApiResponse("Profile deleted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/complete")
    public ResponseEntity<ApiResponse> getCompleteProfiles() {
        List<EmployeeProfile> profiles = employeeProfileService.getCompleteProfiles();
        List<EmployeeProfileDTO> profileDTOs = profiles.stream()
                .map(employeeProfileService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse("Complete profiles retrieved successfully", profileDTOs));
    }

    @GetMapping("/location")
    public ResponseEntity<ApiResponse> getProfilesByLocation(@RequestParam String country, @RequestParam String city) {
        List<EmployeeProfile> profiles = employeeProfileService.getProfilesByLocation(country, city);
        List<EmployeeProfileDTO> profileDTOs = profiles.stream()
                .map(employeeProfileService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse("Profiles retrieved successfully for location", profileDTOs));
    }

    @PutMapping("/{id}/mark-complete")
    public ResponseEntity<ApiResponse> markProfileAsComplete(@PathVariable Long id) {
        try {
            EmployeeProfile profile = employeeProfileService.markProfileAsComplete(id);
            EmployeeProfileDTO profileDTO = employeeProfileService.convertToDTO(profile);
            return ResponseEntity.ok(new ApiResponse("Profile marked as complete successfully", profileDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/ai-recommendations")
    public ResponseEntity<ApiResponse> getProfilesForAIRecommendation() {
        List<EmployeeProfile> profiles = employeeProfileService.getProfilesForAIRecommendation();
        List<EmployeeProfileDTO> profileDTOs = profiles.stream()
                .map(employeeProfileService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse("Profiles for AI recommendation retrieved successfully", profileDTOs));
    }
}