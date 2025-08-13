package com.example.AIProject.controllers;

import com.example.AIProject.dto.UserDto;
import com.example.AIProject.requests.user.CreateUserRequest;
import com.example.AIProject.requests.user.UpdatePasswordRequest;
import com.example.AIProject.requests.user.UpdateUserRequest;
import com.example.AIProject.responses.ApiResponse;
import com.example.AIProject.services.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/users")
public class UserController {

    private final IUserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse("Liste des utilisateurs récupérée avec succès", users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long id) {
        Optional<UserDto> user = userService.getUserById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(new ApiResponse("Utilisateur trouvé", user.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse("Utilisateur non trouvé", null));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse> getUserByEmail(@PathVariable String email) {
        Optional<UserDto> user = userService.getUserByEmail(email);
        if (user.isPresent()) {
            return ResponseEntity.ok(new ApiResponse("Utilisateur trouvé", user.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse("Utilisateur non trouvé", null));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createUser(@RequestBody CreateUserRequest request) {
        if (userService.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse("Un utilisateur avec cet email existe déjà", null));
        }

        UserDto createdUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Utilisateur créé avec succès", createdUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        try {
            UserDto updatedUser = userService.updateUser(id, request);
            return ResponseEntity.ok(new ApiResponse("Utilisateur mis à jour avec succès", updatedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    // Ajoutez cette méthode dans UserController
    @PutMapping("/{id}/password")
    public ResponseEntity<ApiResponse> updatePassword(@PathVariable Long id, @RequestBody UpdatePasswordRequest request) {
        try {
            UserDto updatedUser = userService.updatePassword(id, request);
            return ResponseEntity.ok(new ApiResponse("Mot de passe mis à jour avec succès", updatedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(new ApiResponse("Utilisateur supprimé avec succès", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/exists/{email}")
    public ResponseEntity<ApiResponse> checkEmailExists(@PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(new ApiResponse("Vérification d'email", exists));
    }
}