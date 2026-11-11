package com.fyp.gridguard.Models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Records every configuration change made to a substation or the system,
 * providing a full audit trail for compliance and debugging.
 *
 * <p>An entry is created whenever a TECHNICAL_WORKER or ADMIN:
 * <ul>
 *   <li>Creates, updates, or deletes a substation</li>
 *   <li>Changes threshold settings on an area</li>
 *   <li>Acknowledges or resolves an alert</li>
 * </ul>
 *
 * <p>Table name: {@code audit_logs}
 */
@Entity
@Table(
    name = "audit_logs",
    indexes = {
        @Index(name = "idx_audit_logs_user_id",       columnList = "user_id"),
        @Index(name = "idx_audit_logs_entity_type",   columnList = "entity_type"),
        @Index(name = "idx_audit_logs_performed_at",  columnList = "performed_at")
    }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "performedBy")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    /** The user who performed the action */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
                foreignKey = @ForeignKey(name = "fk_audit_logs_user"))
    private Users performedBy;

    /**
     * Type of entity that was changed,
     * e.g. "Substations", "Areas", "Alert", "Users"
     */
    @Column(name = "entity_type", nullable = false, length = 60)
    private String entityType;

    /** Primary key of the changed entity */
    @Column(name = "entity_id")
    private Long entityId;

    /**
     * Action performed: "CREATE", "UPDATE", "DELETE",
     * "ACKNOWLEDGE_ALERT", "RESOLVE_ALERT", etc.
     */
    @Column(name = "action", nullable = false, length = 60)
    private String action;

    /** JSON snapshot of the entity state before the change */
    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    /** JSON snapshot of the entity state after the change */
    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    /** IP address of the client that made the request */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @CreationTimestamp
    @Column(name = "performed_at", nullable = false, updatable = false)
    private LocalDateTime performedAt;
}
