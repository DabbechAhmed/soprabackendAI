package com.example.AIProject.entities;

    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import org.hibernate.annotations.CreationTimestamp;
    import org.hibernate.annotations.UpdateTimestamp;

    import java.time.LocalDateTime;
    import java.util.Collection;
    import java.util.HashSet;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Entity
    @Table(name = "users")
    public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String password; // BCrypt encrypted

        @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
        @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
                inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
        private Collection<Role> roles = new HashSet<>();

        @Column(unique = true, nullable = false, length = 100)
        private String email;

        @Column(name = "full_name", nullable = false, length = 100)
        private String fullName;

        @Column(name = "is_active")
        private Boolean isActive = true;

        @CreationTimestamp
        @Column(name = "created_at")
        private LocalDateTime createdAt;

        @UpdateTimestamp
        @Column(name = "updated_at")
        private LocalDateTime updatedAt;

        @Column(name = "token_version", nullable = false)
        private int tokenVersion = 0;

        // NOUVEAU: Branche actuelle de l'employé Sopra Tunisia
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "current_branch_id")
        private Branch currentBranch;
        @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
        private Image image;

        // Relationships
        @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private EmployeeProfile employeeProfile;


    }