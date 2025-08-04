package com.example.AIProject.services.image;

import com.example.AIProject.dto.ImageDto;
import com.example.AIProject.entities.Image;
import com.example.AIProject.entities.User;
import com.example.AIProject.exceptions.ResourceNotFoundException;
import com.example.AIProject.repository.ImageRepository;
import com.example.AIProject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService {
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    @Override
    public Image getImageById(Long id) {
        return imageRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Image not found"));
    }

    @Transactional
    @Override
    public ImageDto saveImage(MultipartFile file, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        try {
            // 1. Gérer correctement l'ancienne image
            if (user.getImage() != null) {
                Image oldImage = user.getImage();
                user.setImage(null); // Dissocier l'image de l'utilisateur
                imageRepository.delete(oldImage);
                imageRepository.flush();
            }

            // 2. Créer et configurer la nouvelle image
            Image image = new Image();
            image.setFileName(file.getOriginalFilename());
            image.setFileType(file.getContentType());
            image.setImage(new SerialBlob(file.getBytes()));

            // 3. Établir la relation bidirectionnelle
            image.setUser(user);
            user.setImage(image);

            // 4. Sauvegarder l'image
            Image savedImage = imageRepository.save(image);

            // 5. Mettre à jour l'URL avec l'ID généré
            String downloadUrl = "/api/v1/images/image/download/" + savedImage.getId();
            savedImage.setDownloadUrl(downloadUrl);
            savedImage = imageRepository.save(savedImage);

            // 6. Préparer et retourner le DTO
            ImageDto imageDto = new ImageDto();
            imageDto.setId(savedImage.getId());
            imageDto.setFileName(savedImage.getFileName());
            imageDto.setDownloadUrl(savedImage.getDownloadUrl());

            return imageDto;
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteImageByUserId(Long id) {
        Image image = imageRepository.findByUserId(id);
        if (image == null) {
            throw new ResourceNotFoundException("Image not found");
        }
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setImage(null); // Dissocier l'image de l'utilisateur
        imageRepository.delete(image);
    }

    @Override
    public Image getImageByUserId(Long userId) {
        return Optional.ofNullable(imageRepository.findByUserId(userId)).orElseThrow(()->new ResourceNotFoundException("Image not found"));
    }
}