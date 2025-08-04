package com.example.AIProject.entities;

import com.example.AIProject.enums.ContractType;
import com.example.AIProject.enums.EducationLevel;
import com.example.AIProject.enums.PositionStatus;
import com.example.AIProject.enums.MobilityType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "positions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String department;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Column(precision = 10, scale = 2)
    private BigDecimal salaryMin;

    @Column(precision = 10, scale = 2)
    private BigDecimal salaryMax;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractType contractType;

    @Column(nullable = false)
    private Integer experienceRequired;

    @Enumerated(EnumType.STRING)
    private EducationLevel educationRequired;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PositionStatus status = PositionStatus.ACTIVE;

    // NOUVEAU: Type de mobilité
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MobilityType mobilityType;

    // MODIFIÉ: La branche cible de la position (obligatoire maintenant)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_branch_id", nullable = false)
    private Branch targetBranch;

    // NOUVEAU: Pour mobilité externe - contact HR de la branche cible
    private String externalHrContact;
    private String externalHrEmail;

    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Méthodes utilitaires pour la logique de mobilité
    public boolean isInternalMobility() {
        return this.mobilityType == MobilityType.INTERNAL;
    }

    public boolean isExternalMobility() {
        return this.mobilityType == MobilityType.EXTERNAL;
    }

    public boolean isTunisiaPosition() {
        return this.targetBranch != null && "Tunisia".equalsIgnoreCase(this.targetBranch.getCountry());
    }

    // Propriétés dérivées de la branche cible
    public String getCountry() {
        return this.targetBranch != null ? this.targetBranch.getCountry() : null;
    }

    public String getCity() {
        return this.targetBranch != null ? this.targetBranch.getCity() : null;
    }

    public String getJobText() {
        StringBuilder jobText = new StringBuilder();
        if (description != null && !description.trim().isEmpty()) {
            jobText.append(description.trim());
        }
        if (requirements != null && !requirements.trim().isEmpty()) {
            if (jobText.length() > 0) {
                jobText.append(" ");
            }
            jobText.append(requirements.trim());
        }
        return jobText.toString();
    }
}