package com.fyp.gridguard.Models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Records an anomalous event detected by the ML anomaly-detection pipeline
 * (e.g. SHAP-based explainability, isolation forest, or federated outlier scoring).
 *
 * <p>An anomaly log entry is created when the ML model flags a data point as
 * significantly deviating from the expected pattern. It captures:
 * <ul>
 *   <li>The detected anomaly score / type</li>
 *   <li>The feature values that contributed most (XAI explanation)</li>
 *   <li>Whether a human subsequently confirmed or dismissed it</li>
 * </ul>
 *
 * <p>This table is the primary source for the Anomaly History panel
 * on the technical-worker dashboard.
 *
 * <p>Table name: {@code anomaly_logs}
 */
@Entity
@Table(
    name = "anomaly_logs",
    indexes = {
        @Index(name = "idx_anomaly_logs_substation_id", columnList = "substation_id"),
        @Index(name = "idx_anomaly_logs_detected_at",   columnList = "detected_at"),
        @Index(name = "idx_anomaly_logs_confirmed",     columnList = "confirmed")
    }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"substation", "area", "linkedAlert"})
public class AnomalyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "anomaly_id")
    private Long anomalyId;

    /** Substation where the anomaly was observed */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "substation_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_anomaly_logs_substation"))
    private Substations substation;

    /** Specific area (optional) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id",
                foreignKey = @ForeignKey(name = "fk_anomaly_logs_area"))
    private Areas area;

    /**
     * Type of anomaly detected, e.g.:
     * "SUDDEN_SPIKE", "SUSTAINED_OVERLOAD", "UNEXPECTED_DROP", "PATTERN_DEVIATION"
     */
    @Column(name = "anomaly_type", nullable = false, length = 60)
    private String anomalyType;

    /** Raw anomaly score (higher = more anomalous; model-dependent) */
    @Column(name = "anomaly_score")
    private Double anomalyScore;

    /** Observed load value at the time of detection (kW) */
    @Column(name = "observed_load_kw")
    private Double observedLoadKw;

    /** Expected load value from the forecast (kW) */
    @Column(name = "expected_load_kw")
    private Double expectedLoadKw;

    /** Deviation percentage: (observed - expected) / expected * 100 */
    @Column(name = "deviation_pct")
    private Double deviationPct;

    /**
     * JSON-encoded XAI (SHAP) feature importance for this anomaly, e.g.:
     * {"hour_of_day": 0.42, "day_of_week": 0.21, "prev_load_kw": 0.18}
     */
    @Column(name = "xai_explanation", columnDefinition = "TEXT")
    private String xaiExplanation;

    /** Name of the ML model / algorithm that raised this anomaly */
    @Column(name = "detected_by_model", length = 60)
    private String detectedByModel;

    /** When the anomaly was first detected (simulation time) */
    @Column(name = "detected_at", nullable = false)
    private LocalDateTime detectedAt;

    /** System timestamp when this record was persisted */
    @CreationTimestamp
    @Column(name = "logged_at", nullable = false, updatable = false)
    private LocalDateTime loggedAt;

    /**
     * Whether a human operator confirmed this as a true anomaly.
     * NULL = not yet reviewed; TRUE = confirmed; FALSE = dismissed (false positive).
     */
    @Column(name = "confirmed")
    private Boolean confirmed;

    /** Notes added by the technician during review */
    @Column(name = "operator_notes", length = 1000)
    private String operatorNotes;

    /** Alert raised as a result of this anomaly (if any) */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linked_alert_id",
                foreignKey = @ForeignKey(name = "fk_anomaly_logs_alert"))
    private Alert linkedAlert;
}
