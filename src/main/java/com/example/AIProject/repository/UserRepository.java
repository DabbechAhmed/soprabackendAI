package com.example.AIProject.repository;

import com.example.AIProject.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    boolean existsByEmail(String email);

   @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'ROLE_HR' AND u.currentBranch.id = :branchId")
   List<User> findHRUsersByBranch(@Param("branchId") Long branchId);
}
