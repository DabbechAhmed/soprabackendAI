package com.example.AIProject.services.auth;

import com.example.AIProject.requests.auth.LoginRequest;
import com.example.AIProject.requests.auth.RegisterRequest;
import com.example.AIProject.responses.JwtResponse;

public interface IAuthService {
    JwtResponse login(LoginRequest loginRequest);

    JwtResponse register(RegisterRequest registerRequest);

    void logout(String token);



}
