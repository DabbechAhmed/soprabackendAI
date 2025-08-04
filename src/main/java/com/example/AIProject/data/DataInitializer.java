package com.example.AIProject.data;

import com.example.AIProject.entities.Branch;
import com.example.AIProject.entities.Role;
import com.example.AIProject.entities.User;
import com.example.AIProject.exceptions.ResourceNotFoundException;
import com.example.AIProject.repository.BranchRepository;
import com.example.AIProject.repository.RoleRepository;
import com.example.AIProject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Transactional
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final BranchRepository branchRepository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Set<String> defaultRoles = Set.of("ROLE_EMPLOYEE", "ROLE_HR");
        createDefaultRoles(defaultRoles);
        createDefaultBranches();
        createDefaultEmployees();
    }


    private void createDefaultBranches() {
        // Créer la branche de Tunisie
        if (branchRepository.findByBranchCode("SOPRA_TUNISIA").isEmpty()) {
            Branch tunisiaBranch = new Branch();
            tunisiaBranch.setBranchCode("SOPRA_TUNISIA");
            tunisiaBranch.setBranchName("Sopra Tunisia");
            tunisiaBranch.setCountry("Tunisia");
            tunisiaBranch.setCity("Tunis");
            tunisiaBranch.setAddress("Avenue Habib Bourguiba, Tunis");
            tunisiaBranch.setContactEmail("contact@sopra-tunisia.com");
            tunisiaBranch.setContactPhone("+216 71 123 456");
            tunisiaBranch.setActive(true);
            branchRepository.save(tunisiaBranch);
        }

        // Créer la branche de France
        if (branchRepository.findByBranchCode("SOPRA_FRANCE").isEmpty()) {
            Branch franceBranch = new Branch();
            franceBranch.setBranchCode("SOPRA_FRANCE");
            franceBranch.setBranchName("Sopra France");
            franceBranch.setCountry("France");
            franceBranch.setCity("Paris");
            franceBranch.setAddress("9 bis rue de Presbourg, 75116 Paris");
            franceBranch.setContactEmail("contact@sopra-france.com");
            franceBranch.setContactPhone("+33 1 40 67 29 29");
            franceBranch.setActive(true);
            branchRepository.save(franceBranch);
        }

        // Créer la branche du Maroc
        if (branchRepository.findByBranchCode("SOPRA_MOROCCO").isEmpty()) {
            Branch moroccoBranch = new Branch();
            moroccoBranch.setBranchCode("SOPRA_MOROCCO");
            moroccoBranch.setBranchName("Sopra Morocco");
            moroccoBranch.setCountry("Morocco");
            moroccoBranch.setCity("Casablanca");
            moroccoBranch.setAddress("Boulevard Moulay Slimane, Casablanca");
            moroccoBranch.setContactEmail("contact@sopra-morocco.com");
            moroccoBranch.setContactPhone("+212 522 123 456");
            moroccoBranch.setActive(true);
            branchRepository.save(moroccoBranch);
        }
    }




    public void createDefaultRoles(Set<String> roles) {
        roles.stream().filter(role -> roleRepository.findByName(role).isEmpty())
                .map(Role::new).forEach(roleRepository::save);

    }

    public void createDefaultEmployees() {
        Role employeeRole = roleRepository.findByName("ROLE_EMPLOYEE")
                .orElseThrow(() -> new ResourceNotFoundException("Employee role not found"));
        Role hrRole = roleRepository.findByName("ROLE_HR")
                .orElseThrow(() -> new ResourceNotFoundException("HR role not found"));

        // Récupérer les branches
        Branch tunisiaBranch = branchRepository.findByBranchCode("SOPRA_TUNISIA")
                .orElseThrow(() -> new RuntimeException("Tunisia branch not found"));
        Branch franceBranch = branchRepository.findByBranchCode("SOPRA_FRANCE")
                .orElseThrow(() -> new RuntimeException("France branch not found"));
        Branch moroccoBranch = branchRepository.findByBranchCode("SOPRA_MOROCCO")
                .orElseThrow(() -> new RuntimeException("Morocco branch not found"));

        // Créer un HR pour chaque branche
        createHRUser("hr.tunisia@sopra-tunisia.com", "HR Tunisia", tunisiaBranch, hrRole);
        createHRUser("hr.france@sopra-france.com", "HR France", franceBranch, hrRole);
        createHRUser("hr.morocco@sopra-morocco.com", "HR Morocco", moroccoBranch, hrRole);

        // Créer des employés et les assigner aux branches
        Branch[] branches = {tunisiaBranch, franceBranch, moroccoBranch};

        for (int i = 1; i <= 15; i++) {
            String email = "employee" + i + "@example.com";
            if (userRepository.findByEmail(email) == null) {
                User user = new User();
                user.setEmail(email);
                user.setPassword(passwordEncoder.encode("password" + i));

                user.setFullName("Employee " + i);

                // Utiliser HashSet au lieu de Set.of()
                user.setRoles(new HashSet<>(Set.of(employeeRole)));
                user.setCurrentBranch(branches[(i - 1) % 3]);

                userRepository.save(user);
            }
        }
    }

    private void createHRUser(String email, String username, Branch branch, Role hrRole) {
        if (userRepository.findByEmail(email) == null) {
            User hrUser = new User();
            hrUser.setEmail(email);
            hrUser.setPassword(passwordEncoder.encode("hrpassword"));

            hrUser.setFullName(username);

            // Utiliser HashSet au lieu de Set.of()
            hrUser.setRoles(new HashSet<>(Set.of(hrRole)));
            hrUser.setCurrentBranch(branch);
            userRepository.save(hrUser);
        }
    }
}
