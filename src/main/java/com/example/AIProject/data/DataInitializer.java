package com.example.AIProject.data;

import com.example.AIProject.entities.*;
import com.example.AIProject.enums.ContractType;
import com.example.AIProject.enums.EducationLevel;
import com.example.AIProject.enums.MobilityType;
import com.example.AIProject.enums.PositionStatus;
import com.example.AIProject.exceptions.ResourceNotFoundException;
import com.example.AIProject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final EmployeeProfileRepository employeeProfileRepository;
    private final PositionRepository positionRepository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Set<String> defaultRoles = Set.of("ROLE_EMPLOYEE", "ROLE_HR");
        createDefaultRoles(defaultRoles);
        createDefaultBranches();
        createDefaultEmployees();
        createTestEmployee(); // NOUVEAU
        createTestPositions(); // NOUVEAU
    }
    private void createTestEmployee() {
        String testEmail = "john.doe@sopra-tunisia.com";

        if (userRepository.findByEmail(testEmail) == null) {
            Role employeeRole = roleRepository.findByName("ROLE_EMPLOYEE")
                    .orElseThrow(() -> new ResourceNotFoundException("Employee role not found"));

            Branch tunisiaBranch = branchRepository.findByBranchCode("SOPRA_TUNISIA")
                    .orElseThrow(() -> new RuntimeException("Tunisia branch not found"));

            // Créer l'utilisateur
            User testUser = new User();
            testUser.setEmail(testEmail);
            testUser.setPassword(passwordEncoder.encode("password123"));
            testUser.setFullName("John Doe");
            testUser.setRoles(new HashSet<>(Set.of(employeeRole)));
            testUser.setCurrentBranch(tunisiaBranch);

            User savedUser = userRepository.save(testUser);

            // Créer le profil employé complet
            EmployeeProfile profile = new EmployeeProfile();
            profile.setUser(savedUser);
            profile.setCvText("Senior Java Developer with 5 years of experience in Spring Boot, " +
                    "microservices architecture, and RESTful APIs. Skilled in PostgreSQL, Docker, " +
                    "and AWS cloud deployment. Experience with React.js for frontend development. " +
                    "Strong knowledge of Agile methodologies and CI/CD pipelines. " +
                    "Passionate about clean code and test-driven development.");

            profile.setExperienceYears(5);
            profile.setEducation(EducationLevel.MASTERS);
            profile.setSkills("Java, Spring Boot, PostgreSQL, Docker, AWS, React.js, Git, " +
                    "Jenkins, JUnit, Mockito, Hibernate, Maven, Microservices, REST APIs");
            profile.setCountry("Tunisia");
            profile.setCity("Tunis");
            profile.setPreferredSalaryMin(new BigDecimal("3000"));
            profile.setPreferredSalaryMax(new BigDecimal("5000"));
            profile.setProfileComplete(true);

            employeeProfileRepository.save(profile);
        }
    }

    private void createTestPositions() {
        Branch tunisiaBranch = branchRepository.findByBranchCode("SOPRA_TUNISIA")
                .orElseThrow(() -> new RuntimeException("Tunisia branch not found"));
        Branch franceBranch = branchRepository.findByBranchCode("SOPRA_FRANCE")
                .orElseThrow(() -> new RuntimeException("France branch not found"));
        Branch moroccoBranch = branchRepository.findByBranchCode("SOPRA_MOROCCO")
                .orElseThrow(() -> new RuntimeException("Morocco branch not found"));

        // Position 1: Très compatible avec John Doe
        if (!positionRepository.existsByTitle("Senior Java Developer")) {
            Position javaPosition = new Position();
            javaPosition.setTitle("Senior Java Developer");
            javaPosition.setDepartment("Software Development");
            javaPosition.setDescription("We are looking for a Senior Java Developer to join our team. " +
                    "You will work on microservices architecture using Spring Boot and develop RESTful APIs.");
            javaPosition.setRequirements("5+ years Java experience, Spring Boot, PostgreSQL, " +
                    "Docker, AWS knowledge preferred. Experience with microservices architecture.");
            javaPosition.setSalaryMin(new BigDecimal("4000"));
            javaPosition.setSalaryMax(new BigDecimal("6000"));
            javaPosition.setContractType(ContractType.CDI);
            javaPosition.setExperienceRequired(5);
            javaPosition.setEducationRequired(EducationLevel.MASTERS);
            javaPosition.setStatus(PositionStatus.ACTIVE);
            javaPosition.setMobilityType(MobilityType.INTERNAL);
            javaPosition.setTargetBranch(franceBranch);

            positionRepository.save(javaPosition);
        }

        // Position 2: Moyennement compatible
        if (!positionRepository.existsByTitle("Frontend Developer")) {
            Position frontendPosition = new Position();
            frontendPosition.setTitle("Frontend Developer");
            frontendPosition.setDepartment("Web Development");
            frontendPosition.setDescription("Looking for a Frontend Developer with React.js experience " +
                    "to build modern web applications.");
            frontendPosition.setRequirements("3+ years React.js experience, JavaScript, HTML5, CSS3, " +
                    "Git knowledge required.");
            frontendPosition.setSalaryMin(new BigDecimal("2500"));
            frontendPosition.setSalaryMax(new BigDecimal("4000"));
            frontendPosition.setContractType(ContractType.CDI);
            frontendPosition.setExperienceRequired(3);
            frontendPosition.setEducationRequired(EducationLevel.BACHELORS);
            frontendPosition.setStatus(PositionStatus.ACTIVE);
            frontendPosition.setMobilityType(MobilityType.INTERNAL);
            frontendPosition.setTargetBranch(tunisiaBranch);

            positionRepository.save(frontendPosition);
        }

        // Position 3: Peu compatible
        if (!positionRepository.existsByTitle("Data Scientist")) {
            Position dataPosition = new Position();
            dataPosition.setTitle("Data Scientist");
            dataPosition.setDepartment("Data Analytics");
            dataPosition.setDescription("We need a Data Scientist to analyze large datasets " +
                    "and build machine learning models.");
            dataPosition.setRequirements("Python, Machine Learning, TensorFlow, Pandas, " +
                    "Statistics background required. PhD preferred.");
            dataPosition.setSalaryMin(new BigDecimal("5000"));
            dataPosition.setSalaryMax(new BigDecimal("7000"));
            dataPosition.setContractType(ContractType.CDI);
            dataPosition.setExperienceRequired(3);
            dataPosition.setEducationRequired(EducationLevel.DOCTORATE);
            dataPosition.setStatus(PositionStatus.ACTIVE);
            dataPosition.setMobilityType(MobilityType.EXTERNAL);
            dataPosition.setTargetBranch(moroccoBranch);

            positionRepository.save(dataPosition);
        }

        // Position 4: Compatible avec mobilité externe
        if (!positionRepository.existsByTitle("Full Stack Developer")) {
            Position fullStackPosition = new Position();
            fullStackPosition.setTitle("Full Stack Developer");
            fullStackPosition.setDepartment("Software Development");
            fullStackPosition.setDescription("Full Stack Developer position for building complete web applications " +
                    "using modern technologies including Java Spring Boot backend and React frontend.");
            fullStackPosition.setRequirements("Java, Spring Boot, React.js, PostgreSQL, " +
                    "Docker experience. Full stack development experience required.");
            fullStackPosition.setSalaryMin(new BigDecimal("3500"));
            fullStackPosition.setSalaryMax(new BigDecimal("5500"));
            fullStackPosition.setContractType(ContractType.CDI);
            fullStackPosition.setExperienceRequired(4);
            fullStackPosition.setEducationRequired(EducationLevel.MASTERS);
            fullStackPosition.setStatus(PositionStatus.ACTIVE);
            fullStackPosition.setMobilityType(MobilityType.EXTERNAL);
            fullStackPosition.setTargetBranch(franceBranch);
            fullStackPosition.setExternalHrContact("Marie Dubois");
            fullStackPosition.setExternalHrEmail("marie.dubois@sopra-france.com");

            positionRepository.save(fullStackPosition);
        }
        // Position 5: Backend Architect - Très compatible
        if (!positionRepository.existsByTitle("Backend Architect")) {
            Position backendArchitectPosition = new Position();
            backendArchitectPosition.setTitle("Backend Architect");
            backendArchitectPosition.setDepartment("Software Architecture");
            backendArchitectPosition.setDescription("We are seeking a Backend Architect to design and implement " +
                    "scalable microservices architecture. Lead technical decisions and mentor development teams.");
            backendArchitectPosition.setRequirements("5+ years Java/Spring Boot, microservices architecture, " +
                    "Docker, Kubernetes, AWS/Azure, PostgreSQL, system design experience required.");
            backendArchitectPosition.setSalaryMin(new BigDecimal("5500"));
            backendArchitectPosition.setSalaryMax(new BigDecimal("7500"));
            backendArchitectPosition.setContractType(ContractType.CDI);
            backendArchitectPosition.setExperienceRequired(6);
            backendArchitectPosition.setEducationRequired(EducationLevel.MASTERS);
            backendArchitectPosition.setStatus(PositionStatus.ACTIVE);
            backendArchitectPosition.setMobilityType(MobilityType.EXTERNAL);
            backendArchitectPosition.setTargetBranch(franceBranch);

            positionRepository.save(backendArchitectPosition);
        }

        // Position 6: DevOps Engineer - Compatible
        if (!positionRepository.existsByTitle("DevOps Engineer")) {
            Position devopsPosition = new Position();
            devopsPosition.setTitle("DevOps Engineer");
            devopsPosition.setDepartment("Infrastructure");
            devopsPosition.setDescription("DevOps Engineer to manage CI/CD pipelines, containerization, " +
                    "and cloud infrastructure. Work with development teams to automate deployment processes.");
            devopsPosition.setRequirements("Docker, Kubernetes, Jenkins, AWS, Git, Linux administration. " +
                    "Experience with Java applications deployment preferred.");
            devopsPosition.setSalaryMin(new BigDecimal("4200"));
            devopsPosition.setSalaryMax(new BigDecimal("6200"));
            devopsPosition.setContractType(ContractType.CDI);
            devopsPosition.setExperienceRequired(4);
            devopsPosition.setEducationRequired(EducationLevel.BACHELORS);
            devopsPosition.setStatus(PositionStatus.ACTIVE);
            devopsPosition.setMobilityType(MobilityType.INTERNAL);
            devopsPosition.setTargetBranch(moroccoBranch);

            positionRepository.save(devopsPosition);
        }

        // Position 7: Spring Boot Consultant - Très compatible
        if (!positionRepository.existsByTitle("Spring Boot Consultant")) {
            Position consultantPosition = new Position();
            consultantPosition.setTitle("Spring Boot Consultant");
            consultantPosition.setDepartment("Consulting");
            consultantPosition.setDescription("Spring Boot Consultant to work with clients on modernizing their " +
                    "Java applications. Design and implement REST APIs and microservices solutions.");
            consultantPosition.setRequirements("Expert level Spring Boot, RESTful APIs, PostgreSQL, " +
                    "microservices, Docker. Client-facing experience and strong communication skills.");
            consultantPosition.setSalaryMin(new BigDecimal("4500"));
            consultantPosition.setSalaryMax(new BigDecimal("6500"));
            consultantPosition.setContractType(ContractType.FREELANCE);
            consultantPosition.setExperienceRequired(5);
            consultantPosition.setEducationRequired(EducationLevel.MASTERS);
            consultantPosition.setStatus(PositionStatus.ACTIVE);
            consultantPosition.setMobilityType(MobilityType.EXTERNAL);
            consultantPosition.setTargetBranch(franceBranch);

            positionRepository.save(consultantPosition);
        }

        // Position 8: Technical Lead Java - Très compatible
        if (!positionRepository.existsByTitle("Technical Lead Java")) {
            Position techLeadPosition = new Position();
            techLeadPosition.setTitle("Technical Lead Java");
            techLeadPosition.setDepartment("Software Development");
            techLeadPosition.setDescription("Technical Lead to guide a team of Java developers. " +
                    "Responsible for code quality, architecture decisions, and team mentoring.");
            techLeadPosition.setRequirements("5+ years Java, Spring Boot, team leadership experience, " +
                    "Agile methodologies, code review, PostgreSQL, Docker knowledge.");
            techLeadPosition.setSalaryMin(new BigDecimal("5000"));
            techLeadPosition.setSalaryMax(new BigDecimal("7000"));
            techLeadPosition.setContractType(ContractType.CDI);
            techLeadPosition.setExperienceRequired(5);
            techLeadPosition.setEducationRequired(EducationLevel.MASTERS);
            techLeadPosition.setStatus(PositionStatus.ACTIVE);
            techLeadPosition.setMobilityType(MobilityType.INTERNAL);
            techLeadPosition.setTargetBranch(tunisiaBranch);

            positionRepository.save(techLeadPosition);
        }

        // Position 9: Software Engineer - Compatible mais junior
        if (!positionRepository.existsByTitle("Software Engineer Java")) {
            Position softwareEngPosition = new Position();
            softwareEngPosition.setTitle("Software Engineer Java");
            softwareEngPosition.setDepartment("Software Development");
            softwareEngPosition.setDescription("Software Engineer to develop and maintain Java applications. " +
                    "Work in an Agile team to deliver high-quality software solutions.");
            softwareEngPosition.setRequirements("3+ years Java, Spring framework, SQL databases, " +
                    "Git, unit testing, basic Docker knowledge preferred.");
            softwareEngPosition.setSalaryMin(new BigDecimal("2800"));
            softwareEngPosition.setSalaryMax(new BigDecimal("4200"));
            softwareEngPosition.setContractType(ContractType.CDI);
            softwareEngPosition.setExperienceRequired(3);
            softwareEngPosition.setEducationRequired(EducationLevel.BACHELORS);
            softwareEngPosition.setStatus(PositionStatus.ACTIVE);
            softwareEngPosition.setMobilityType(MobilityType.INTERNAL);
            softwareEngPosition.setTargetBranch(tunisiaBranch);

            positionRepository.save(softwareEngPosition);
        }

        // Position 10: Cloud Solutions Engineer - Compatible
        if (!positionRepository.existsByTitle("Cloud Solutions Engineer")) {
            Position cloudPosition = new Position();
            cloudPosition.setTitle("Cloud Solutions Engineer");
            cloudPosition.setDepartment("Cloud Services");
            cloudPosition.setDescription("Design and implement cloud-based solutions using AWS services. " +
                    "Migrate existing Java applications to cloud infrastructure.");
            cloudPosition.setRequirements("AWS services, Java applications deployment, Docker, " +
                    "Kubernetes, PostgreSQL on cloud, CI/CD pipelines.");
            cloudPosition.setSalaryMin(new BigDecimal("4800"));
            cloudPosition.setSalaryMax(new BigDecimal("6800"));
            cloudPosition.setContractType(ContractType.CDI);
            cloudPosition.setExperienceRequired(4);
            cloudPosition.setEducationRequired(EducationLevel.MASTERS);
            cloudPosition.setStatus(PositionStatus.ACTIVE);
            cloudPosition.setMobilityType(MobilityType.EXTERNAL);
            cloudPosition.setTargetBranch(moroccoBranch);

            positionRepository.save(cloudPosition);
        }
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
