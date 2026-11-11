package com.fyp.gridguard.Models;

import com.fyp.gridguard.Utilities.AlertSeverity;
import com.fyp.gridguard.Utilities.AlertStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * System alert generated automatically by the TickEngine or ML anomaly detector
 * when a threshold is breached (e.g. overload, voltage sag, anomalous consumption).
 *
 * <p>Alerts can also be created manually by a TECHNICAL_WORKER.
 *
 * <p>Life-cycle:  OPEN → ACKNOWLEDGED → RESOLVED
 *
 * <p>Table name: {@code alerts}
 */
@Entity
@Table(
    name = "alerts",
    indexes = {
        @Index(name = "idx_alerts_substation_id", columnList = "substation_id"),
        @Index(name = "idx_alerts_status",         columnList = "status"),
        @Index(name = "idx_alerts_triggered_at",   columnList = "triggered_at")
    }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"substation", "resolvedBy"})
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private Long alertId;

    /** The substation that caused this alert (nullable if area-level) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "substation_id",
                foreignKey = @ForeignKey(name = "fk_alerts_substation"))
    private Substations substation;

    /** The area that caused this alert (nullable if substation-level) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id",
                foreignKey = @ForeignKey(name = "fk_alerts_area"))
    private Areas area;

    /** Short machine-readable type, e.g. "OVERLOAD", "VOLTAGE_SAG", "ANOMALY" */
    @Column(name = "alert_type", nullable = false, length = 50)
    private String alertType;

    /** Severity of the alert */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertSeverity severity;

    /** Current lifecycle status */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertStatus status = AlertStatus.OPEN;

    /** Human-readable description of the alert condition */
    @Column(nullable = false, length = 500)
    private String message;

    /** Actual load value that triggered the alert (kW), for auditing */
    @Column(name = "triggered_load_kw")
    private Double triggeredLoadKw;

    /** Threshold value that was exceeded */
    @Column(name = "threshold_kw")
    private Double thresholdKw;

    /** When the alert condition first occurred */
    @CreationTimestamp
    @Column(name = "triggered_at", nullable = false, updatable = false)
    private LocalDateTime triggeredAt;

    /** When a worker acknowledged the alert */
    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;

    /** When the condition cleared and the alert was resolved */
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    /** Worker who resolved the alert */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by_user_id",
                foreignKey = @ForeignKey(name = "fk_alerts_resolved_by"))
    private Users resolvedBy;

    /** Free-text notes added by the technician */
    @Column(name = "resolution_notes", length = 1000)
    private String resolutionNotes;

    /** Whether this alert originated from the ML anomaly detector */
    @Column(name = "ml_generated", nullable = false)
    private boolean mlGenerated = false;
}
