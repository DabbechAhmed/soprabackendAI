package com.example.AIProject.requests.user;

    import lombok.Data;

    @Data
    public class CreateUserRequest {
        private String email;
        private String fullName;
        private String password;
    }