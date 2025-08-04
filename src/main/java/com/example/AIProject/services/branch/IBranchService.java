package com.example.AIProject.services.branch;

import com.example.AIProject.entities.Branch;

import java.util.List;
import java.util.Optional;

public interface IBranchService {
    List<Branch> getAllBranches();
    Optional<Branch> getBranchById(Long id);
    Optional<Branch> getBranchByCode(String branchCode);
    Branch createBranch(Branch branch);
    Branch updateBranch(Long id, Branch branch);
    void deleteBranch(Long id);
    List<Branch> getActiveBranches();
    List<Branch> getBranchesByCountry(String country);
}
