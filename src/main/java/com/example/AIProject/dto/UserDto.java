package com.example.AIProject.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserDto {
    private Long id;
    private String email;
    private String fullName;
    private List<String> roles;
    private Boolean isActive;
    // Other user fields as needed
}
