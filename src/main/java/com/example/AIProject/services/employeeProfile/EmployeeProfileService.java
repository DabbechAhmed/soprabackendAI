package com.example.AIProject.services.employeeProfile;

import com.example.AIProject.dto.EmployeeProfileDTO;
import com.example.AIProject.entities.EmployeeProfile;
import com.example.AIProject.entities.User;
import com.example.AIProject.repository.EmployeeProfileRepository;
import com.example.AIProject.repository.UserRepository;
import com.example.AIProject.requests.employeeProfile.CreateEmployeeProfileRequest;
import com.example.AIProject.requests.employeeProfile.UpdateEmployeeProfileRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeProfileService implements IEmployeeProfileService {

    private final EmployeeProfileRepository employeeProfileRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeProfile> getAllProfiles() {
        return employeeProfileRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmployeeProfile> getProfileById(Long id) {
        return employeeProfileRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmployeeProfile> getProfileByUserId(Long userId) {
        return employeeProfileRepository.findByUserId(userId);
    }

    @Override
    public EmployeeProfile createProfile(CreateEmployeeProfileRequest request) {
        // Vérifier que l'utilisateur existe
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        // Vérifier qu'un profil n'existe pas déjà pour cet utilisateur
        if (employeeProfileRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new IllegalArgumentException("Profile already exists for user ID: " + request.getUserId());
        }

        EmployeeProfile profile = new EmployeeProfile();
        profile.setUser(user);
        profile.setCvText(request.getCvText());
        profile.setExperienceYears(request.getExperienceYears());
        profile.setEducation(request.getEducation());
        profile.setSkills(request.getSkills());
        profile.setCountry(request.getCountry());
        profile.setCity(request.getCity());
        profile.setPreferredSalaryMin(request.getPreferredSalaryMin());
        profile.setPreferredSalaryMax(request.getPreferredSalaryMax());

        // Vérifier si le profil est complet automatiquement
        checkAndSetProfileComplete(profile);

        return employeeProfileRepository.save(profile);
    }

    @Override
    public EmployeeProfile updateProfile(Long id, UpdateEmployeeProfileRequest request) {
        return employeeProfileRepository.findById(id)
                .map(existingProfile -> {
                    if (request.getCvText() != null) {
                        existingProfile.setCvText(request.getCvText());
                    }
                    if (request.getExperienceYears() != null) {
                        existingProfile.setExperienceYears(request.getExperienceYears());
                    }
                    if (request.getEducation() != null) {
                        existingProfile.setEducation(request.getEducation());
                    }
                    if (request.getSkills() != null) {
                        existingProfile.setSkills(request.getSkills());
                    }
                    if (request.getCountry() != null) {
                        existingProfile.setCountry(request.getCountry());
                    }
                    if (request.getCity() != null) {
                        existingProfile.setCity(request.getCity());
                    }
                    if (request.getPreferredSalaryMin() != null) {
                        existingProfile.setPreferredSalaryMin(request.getPreferredSalaryMin());
                    }
                    if (request.getPreferredSalaryMax() != null) {
                        existingProfile.setPreferredSalaryMax(request.getPreferredSalaryMax());
                    }

                    // Vérifier si le profil devient complet après mise à jour
                    checkAndSetProfileComplete(existingProfile);

                    return employeeProfileRepository.save(existingProfile);
                })
                .orElseThrow(() -> new RuntimeException("Employee profile not found with id: " + id));
    }

    @Override
    public void deleteProfile(Long id) {
        if (!employeeProfileRepository.existsById(id)) {
            throw new RuntimeException("Employee profile not found with id: " + id);
        }
        employeeProfileRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeProfile> getCompleteProfiles() {
        return employeeProfileRepository.findCompleteProfiles();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeProfile> getProfilesByLocation(String country, String city) {
        return employeeProfileRepository.findByCountryAndCity(country, city);
    }

    @Override
    public EmployeeProfile markProfileAsComplete(Long id) {
        return employeeProfileRepository.findById(id)
                .map(profile -> {
                    profile.setProfileComplete(true);
                    return employeeProfileRepository.save(profile);
                })
                .orElseThrow(() -> new RuntimeException("Employee profile not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeProfile> getProfilesForAIRecommendation() {
        return employeeProfileRepository.findCompleteProfiles();
    }

    /**
     * Méthode privée pour vérifier et marquer automatiquement un profil comme complet
     */
    private void checkAndSetProfileComplete(EmployeeProfile profile) {
        boolean isComplete = profile.getCvText() != null && !profile.getCvText().trim().isEmpty() &&
                           profile.getExperienceYears() != null &&
                           profile.getEducation() != null &&
                           profile.getSkills() != null && !profile.getSkills().trim().isEmpty() &&
                           profile.getCountry() != null && !profile.getCountry().trim().isEmpty() &&
                           profile.getCity() != null && !profile.getCity().trim().isEmpty();

        profile.setProfileComplete(isComplete);
    }

    @Override
    public EmployeeProfileDTO convertToDTO(EmployeeProfile profile) {
        return modelMapper.map(profile, EmployeeProfileDTO.class);
    }
}