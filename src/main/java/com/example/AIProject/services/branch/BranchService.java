package com.example.AIProject.services.branch;

import com.example.AIProject.entities.Branch;
import com.example.AIProject.exceptions.ResourceNotFoundException;
import com.example.AIProject.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BranchService implements IBranchService {

    private final BranchRepository branchRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Branch> getBranchById(Long id) {
        return branchRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Branch> getBranchByCode(String branchCode) {
        return branchRepository.findByBranchCode(branchCode);
    }

    @Override
    public Branch createBranch(Branch branch) {
        // Validation: vérifier que le branchCode n'existe pas déjà
        if (branchRepository.findByBranchCode(branch.getBranchCode()).isPresent()) {
            throw new IllegalArgumentException("Branch with code " + branch.getBranchCode() + " already exists");
        }
        return branchRepository.save(branch);
    }

    @Override
    public Branch updateBranch(Long id, Branch branch) {
        return branchRepository.findById(id)
                .map(existingBranch -> {
                    existingBranch.setBranchName(branch.getBranchName());
                    existingBranch.setCountry(branch.getCountry());
                    existingBranch.setCity(branch.getCity());
                    existingBranch.setAddress(branch.getAddress());
                    existingBranch.setContactEmail(branch.getContactEmail());
                    existingBranch.setContactPhone(branch.getContactPhone());
                    existingBranch.setActive(branch.getActive());
                    return branchRepository.save(existingBranch);
                })
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + id));
    }

    @Override
    public void deleteBranch(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + id));
        // On peut la désactiver
        branch.setActive(false);
        branchRepository.save(branch);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Branch> getActiveBranches() {
        return branchRepository.findByActive(true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Branch> getBranchesByCountry(String country) {
        return branchRepository.findByCountry(country);
    }
}