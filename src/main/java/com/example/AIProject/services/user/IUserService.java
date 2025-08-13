package com.example.AIProject.services.user;

import com.example.AIProject.dto.UserDto;
import com.example.AIProject.requests.user.CreateUserRequest;
import com.example.AIProject.requests.user.UpdatePasswordRequest;
import com.example.AIProject.requests.user.UpdateUserRequest;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    List<UserDto> getAllUsers();
    Optional<UserDto> getUserById(Long id);
    Optional<UserDto> getUserByEmail(String email);
    UserDto createUser(CreateUserRequest request);
    UserDto updateUser(Long id, UpdateUserRequest request);
    void deleteUser(Long id);
    boolean existsByEmail(String email);

    // Ajoutez cette méthode dans UserService
    UserDto updatePassword(Long userId, UpdatePasswordRequest request);
}