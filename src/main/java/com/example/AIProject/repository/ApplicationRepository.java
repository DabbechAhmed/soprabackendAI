package com.example.AIProject.repository;

import com.example.AIProject.entities.Application;
import com.example.AIProject.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByUserId(Long userId);
    List<Application> findByPositionId(Long positionId);
    List<Application> findByStatus(ApplicationStatus status);
    Optional<Application> findByUserIdAndPositionId(Long userId, Long positionId);

    @Query("SELECT a FROM Application a WHERE a.user.id = :userId AND a.status = :status")
    List<Application> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") ApplicationStatus status);

    @Query("SELECT a FROM Application a WHERE a.position.id = :positionId AND a.status = :status")
    List<Application> findByPositionIdAndStatus(@Param("positionId") Long positionId, @Param("status") ApplicationStatus status);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.user.id = :userId")
    long countApplicationsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.position.id = :positionId")
    long countApplicationsByPositionId(@Param("positionId") Long positionId);

    List<Application> findByPositionIdAndStatusAndIdNot(Long positionId, ApplicationStatus applicationStatus, Long acceptedApplicationId);
}