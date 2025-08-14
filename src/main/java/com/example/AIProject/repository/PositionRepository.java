package com.example.AIProject.repository;

import com.example.AIProject.entities.Position;
import com.example.AIProject.enums.MobilityType;
import com.example.AIProject.enums.PositionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Long> {
    List<Position> findByStatus(PositionStatus status);
    List<Position> findByDepartment(String department);
    Optional<Position> findByTitle(String title);

    @Query("SELECT p FROM Position p WHERE p.status = 'ACTIVE'")
    List<Position> findActivePositions();

    // CORRIGÉ: Utiliser la relation avec Branch
    @Query("SELECT p FROM Position p WHERE p.targetBranch.country = :country AND p.targetBranch.city = :city")
    List<Position> findByCountryAndCity(@Param("country") String country, @Param("city") String city);

    @Query("SELECT p FROM Position p WHERE p.status = 'ACTIVE' AND p.targetBranch.country = :country AND p.targetBranch.city = :city")
    List<Position> findActivePositionsByLocation(@Param("country") String country, @Param("city") String city);

    // Ajout de méthodes pour la mobilité
    @Query("SELECT p FROM Position p WHERE p.status = 'ACTIVE' AND p.mobilityType = :mobilityType")
    List<Position> findActivePositionsByMobilityType(@Param("mobilityType") MobilityType mobilityType);

    @Query("SELECT p FROM Position p WHERE p.status = 'ACTIVE' AND p.mobilityType = 'INTERNAL' AND p.targetBranch.country = 'Tunisia'")
    List<Position> findInternalTunisiaMobilityPositions();

    @Query("SELECT p FROM Position p WHERE p.status = 'ACTIVE' AND p.mobilityType = 'EXTERNAL' AND p.targetBranch.country != 'Tunisia'")
    List<Position> findExternalMobilityPositions();

    @Query("SELECT p FROM Position p WHERE p.status = 'ACTIVE' AND p.targetBranch.country = :country")
    List<Position> findActivePositionsByCountry(@Param("country") String country);

    @Query("SELECT p FROM Position p WHERE p.status = 'ACTIVE' AND p.targetBranch.country = :country AND p.targetBranch.city = :city")
    List<Position> findActivePositionsByCountryAndCity(@Param("country") String country, @Param("city") String city);

    List<Position> findByTargetBranchIdAndStatus(Long branchId, PositionStatus status);
    boolean existsByTitle(String title);
}