package com.example.AIProject.repository;

import com.example.AIProject.entities.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    Optional<Branch> findByBranchCode(String branchCode);
    List<Branch> findByCountry(String country);
    List<Branch> findByActive(Boolean active);

    @Query("SELECT b FROM Branch b WHERE b.active = true AND b.country != 'Tunisia'")
    List<Branch> findActiveNonTunisiaBranches();

    @Query("SELECT b FROM Branch b WHERE b.active = true AND b.country = 'Tunisia'")
    List<Branch> findActiveTunisiaBranches();
}