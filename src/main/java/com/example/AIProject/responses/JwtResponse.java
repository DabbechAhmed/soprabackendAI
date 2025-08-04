package com.example.AIProject.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponse {
    private Long id;
    private String token;
}
