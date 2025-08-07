package com.example.AIProject.controllers;

import com.example.AIProject.dto.ImageDto;
import com.example.AIProject.entities.Image;
import com.example.AIProject.exceptions.ResourceNotFoundException;
import com.example.AIProject.responses.ApiResponse;
import com.example.AIProject.services.image.IImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/images")
public class ImageController {
    private final IImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> saveImages(@RequestParam MultipartFile file, @RequestParam Long userId) {
        try {
            ImageDto imageDto = imageService.saveImage(file, userId);
            return ResponseEntity.ok(new ApiResponse("Uploaded successfully", imageDto));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/image/download/{imageId}")
    public ResponseEntity<Resource> downloadImage(@PathVariable Long imageId) throws SQLException {
        Image image=imageService.getImageById(imageId);
        ByteArrayResource resource=new ByteArrayResource(image.getImage().getBytes(1, (int) image.getImage().length()));
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(image.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/user/{userId}/image")
    public ResponseEntity<?> downloadImageByUserId(@PathVariable Long userId) throws SQLException {
        try {
            Image image=imageService.getImageByUserId(userId);
            ByteArrayResource resource=new ByteArrayResource(image.getImage().getBytes(1, (int) image.getImage().length()));
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(image.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFileName() + "\"")
                    .body(resource);
        } catch (SQLException e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NO_CONTENT).body(new ApiResponse(e.getMessage(), null));
        }

    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deleteImageByUserId(@RequestParam Long userId) {
        try {
            imageService.deleteImageByUserId(userId);
            return ResponseEntity.ok(new ApiResponse("Image deleted successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NO_CONTENT).body(new ApiResponse(e.getMessage(), null));
        }
    }

}
