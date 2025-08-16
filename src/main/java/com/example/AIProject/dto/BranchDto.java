package com.example.AIProject.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class BranchDto {
    private Long id;
    private String branchCode;
    private String branchName;
    private String country;
    private String city;
    private String address;
    private String contactEmail;
    private String contactPhone;
    private Boolean active;
}
