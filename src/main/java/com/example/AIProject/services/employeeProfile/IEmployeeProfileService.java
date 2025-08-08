package com.example.AIProject.services.employeeProfile;

import com.example.AIProject.dto.EmployeeProfileDTO;
import com.example.AIProject.entities.EmployeeProfile;
import com.example.AIProject.requests.employeeProfile.CreateEmployeeProfileRequest;
import com.example.AIProject.requests.employeeProfile.UpdateEmployeeProfileRequest;
import java.util.List;
import java.util.Optional;

public interface IEmployeeProfileService {
    List<EmployeeProfile> getAllProfiles();
    Optional<EmployeeProfile> getProfileById(Long id);
    Optional<EmployeeProfile> getProfileByUserId(Long userId);
    EmployeeProfile createProfile(CreateEmployeeProfileRequest request);
    EmployeeProfile updateProfile(Long id, UpdateEmployeeProfileRequest request);
    void deleteProfile(Long id);
    List<EmployeeProfile> getCompleteProfiles();
    List<EmployeeProfile> getProfilesByLocation(String country, String city);
    EmployeeProfile markProfileAsComplete(Long id);
    List<EmployeeProfile> getProfilesForAIRecommendation();

    EmployeeProfileDTO convertToDTO(EmployeeProfile profile);
}