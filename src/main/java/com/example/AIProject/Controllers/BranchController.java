package com.example.AIProject.Controllers;

import com.example.AIProject.entities.Branch;
import com.example.AIProject.responses.ApiResponse;
import com.example.AIProject.services.branch.IBranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("${api.prefix}/branches")
@RequiredArgsConstructor
public class BranchController {

    private final IBranchService branchService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllBranches() {
        List<Branch> branches = branchService.getAllBranches();
        return ResponseEntity.ok(new ApiResponse("Branches retrieved successfully", branches));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse> getActiveBranches() {
        List<Branch> activeBranches = branchService.getActiveBranches();
        return ResponseEntity.ok(new ApiResponse("Active branches retrieved successfully", activeBranches));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getBranchById(@PathVariable Long id) {
        return branchService.getBranchById(id)
                .map(branch -> ResponseEntity.ok(new ApiResponse("Branch retrieved successfully", branch)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("Branch not found with id: " + id, null)));
    }

    @GetMapping("/code/{branchCode}")
    public ResponseEntity<ApiResponse> getBranchByCode(@PathVariable String branchCode) {
        return branchService.getBranchByCode(branchCode)
                .map(branch -> ResponseEntity.ok(new ApiResponse("Branch retrieved successfully", branch)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("Branch not found with code: " + branchCode, null)));
    }

    @GetMapping("/country/{country}")
    public ResponseEntity<ApiResponse> getBranchesByCountry(@PathVariable String country) {
        List<Branch> branches = branchService.getBranchesByCountry(country);
        return ResponseEntity.ok(new ApiResponse("Branches retrieved successfully for country: " + country, branches));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createBranch(@Valid @RequestBody Branch branch) {
        try {
            Branch createdBranch = branchService.createBranch(branch);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Branch created successfully", createdBranch));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateBranch(@PathVariable Long id, @Valid @RequestBody Branch branch) {
        try {
            Branch updatedBranch = branchService.updateBranch(id, branch);
            return ResponseEntity.ok(new ApiResponse("Branch updated successfully", updatedBranch));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteBranch(@PathVariable Long id) {
        try {
            branchService.deleteBranch(id);
            return ResponseEntity.ok(new ApiResponse("Branch deactivated successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }
}