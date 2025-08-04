package com.example.AIProject.services.image;

import com.example.AIProject.dto.ImageDto;
import com.example.AIProject.entities.Image;

import org.springframework.web.multipart.MultipartFile;

public interface IImageService {
    Image getImageById(Long id);
    public ImageDto saveImage(MultipartFile file, Long userId);

    void deleteImageByUserId(Long id);

    public Image getImageByUserId(Long userId);
}
