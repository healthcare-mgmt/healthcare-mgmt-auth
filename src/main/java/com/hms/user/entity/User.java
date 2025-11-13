package com.hms.user.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    // Password field used by Spring Security for local (non-OAuth) users
    @Column(nullable = true)
    private String password;

    // For OAuth2 users (e.g., Google)
    private String provider;    // "google", "local", etc.
    private String providerId;  // Google sub ID or local user ID

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
    }

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Patient patientProfile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Doctor doctorProfile;

}
