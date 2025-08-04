package com.example.AIProject.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "branches")
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String branchCode; // e.g., "SOPRA_TUNISIA", "SOPRA_FRANCE"

    @Column(nullable = false)
    private String branchName; // e.g., "Sopra Tunisia", "Sopra France"

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String city;

    private String address;
    private String contactEmail;
    private String contactPhone;

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "currentBranch", fetch = FetchType.LAZY)
    private List<User> employees;
}