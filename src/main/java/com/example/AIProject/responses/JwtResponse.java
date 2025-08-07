package com.example.AIProject.responses;

import com.example.AIProject.dto.UserDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponse {
    private UserDto user;
    private String token;
}
