package com.fyp.gridguard.Models;

import com.fyp.gridguard.Utilities.Roles;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Represents a system user (grid viewer or technical worker).
 * Roles:
 *   - USER             → read-only dashboard access
 *   - TECHNICAL_WORKER → full CRUD on substations and configuration
 */
@Entity
@Table(
    name = "users",
    uniqueConstraints = @UniqueConstraint(name = "uq_users_email", columnNames = "email")
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    /** Full display name, e.g. "Ali Hassan" */
    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    /** Unique login identifier */
    @Column(nullable = false, unique = true, length = 180)
    private String email;

    /** BCrypt-hashed password – never stored in plain text */
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Roles roles;

    /** Whether the account is active (soft-disable without deleting) */
    @Column(nullable = false)
    private boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // -----------------------------------------------------------------
    // Convenience constructor used in AuthController / UserService
    // -----------------------------------------------------------------
    public Users(String fullName, String email, String password, Roles roles) {
        this.fullName = fullName;
        this.email    = email;
        this.password = password;
        this.roles    = roles;
    }
}
