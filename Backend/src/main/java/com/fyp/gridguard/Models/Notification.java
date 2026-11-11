package com.fyp.gridguard.Models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * In-app notification delivered to a specific user (or broadcast to a role group).
 *
 * <p>Notifications are created automatically when:
 * <ul>
 *   <li>A new {@link Alert} is raised (overload, anomaly, etc.)</li>
 *   <li>An FL training round completes</li>
 *   <li>A technical worker resolves an alert</li>
 * </ul>
 *
 * <p>They can also be sent manually from the admin panel.
 *
 * <p>Table name: {@code notifications}
 */
@Entity
@Table(
    name = "notifications",
    indexes = {
        @Index(name = "idx_notifications_user_id",    columnList = "user_id"),
        @Index(name = "idx_notifications_is_read",    columnList = "is_read"),
        @Index(name = "idx_notifications_created_at", columnList = "created_at")
    }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "user")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    /** Target user; null means broadcast to all users of the linked role */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
                foreignKey = @ForeignKey(name = "fk_notifications_user"))
    private Users user;

    /** Short title shown in the notification badge */
    @Column(nullable = false, length = 150)
    private String title;

    /** Full notification body */
    @Column(nullable = false, length = 1000)
    private String message;

    /**
     * Type tag for icon / routing on the frontend,
     * e.g. "ALERT", "ANOMALY", "FL_ROUND_COMPLETE", "SYSTEM"
     */
    @Column(name = "notification_type", nullable = false, length = 40)
    private String notificationType;

    /** Whether the user has viewed / dismissed this notification */
    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    /** Optional deep-link to the relevant resource, e.g. "/alerts/42" */
    @Column(name = "action_url", length = 300)
    private String actionUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** When the user read this notification */
    @Column(name = "read_at")
    private LocalDateTime readAt;
}
