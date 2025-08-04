package com.example.AIProject.repository;

import com.example.AIProject.entities.Position;
import com.example.AIProject.enums.PositionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Long> {
    List<Position> findByStatus(PositionStatus status);
    List<Position> findByCountryAndCity(String country, String city);
    List<Position> findByDepartment(String department);

    @Query("SELECT p FROM Position p WHERE p.status = 'ACTIVE'")
    List<Position> findActivePositions();

    @Query("SELECT p FROM Position p WHERE p.status = 'ACTIVE' AND p.country = :country AND p.city = :city")
    List<Position> findActivePositionsByLocation(@Param("country") String country, @Param("city") String city);
}