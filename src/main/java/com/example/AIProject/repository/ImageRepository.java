package com.example.AIProject.repository;

import com.example.AIProject.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findByUserId(Long id);
}
