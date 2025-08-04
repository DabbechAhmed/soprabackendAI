package com.example.AIProject.repository;

import com.example.AIProject.entities.EmployeeProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployeeProfileRepository extends JpaRepository<EmployeeProfile, Long> {
    Optional<EmployeeProfile> findByUserId(Long userId);

    @Query("SELECT ep FROM EmployeeProfile ep WHERE ep.profileComplete = true AND ep.cvText IS NOT NULL")
    List<EmployeeProfile> findCompleteProfiles();

    List<EmployeeProfile> findByCountryAndCity(String country, String city);
}