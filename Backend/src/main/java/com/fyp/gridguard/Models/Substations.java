package com.fyp.gridguard.Models;

import com.fyp.gridguard.Utilities.AssetStatus;
import com.fyp.gridguard.Utilities.SubProfileType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an electrical substation in the smart grid.
 *
 * <p>A substation:
 * <ul>
 *   <li>Has a fixed rated capacity ({@link #capacityKw})</li>
 *   <li>Tracks real-time aggregate load ({@link #currentLoadKw}) updated by the TickEngine</li>
 *   <li>Contains one or more {@link Areas} that draw power from it</li>
 *   <li>Stores historical readings in {@link PowerReadings}</li>
 * </ul>
 */
@Entity
@Table(
    name = "substations",
    uniqueConstraints = @UniqueConstraint(name = "uq_substations_code", columnNames = "code")
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"areas", "powerReadings"})
public class Substations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "substation_id")
    private Long substationId;

    /** Unique short code, e.g. "SUB-N1" */
    @Column(nullable = false, unique = true, length = 30)
    private String code;

    /** Human-readable name, e.g. "North Substation" */
    @Column(nullable = false, length = 120)
    private String name;

    /**
     * Load-profile type of this substation.
     * Stored as a plain String so old rows survive enum renames.
     * Use {@link SubProfileType} constants for type-safe comparisons.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "sub_profile_type", nullable = false, length = 30)
    private SubProfileType subProfileType;

    /** Operational health (HEALTHY / WARNING / CRITICAL / OFFLINE) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssetStatus status = AssetStatus.UNKNOWN;

    /** Rated maximum capacity in kilowatts */
    @Column(name = "capacity_kw")
    private Double capacityKw;

    /** Aggregate real-time load in kW; updated by the TickEngine every 5 seconds */
    @Column(name = "current_load_kw")
    private Double currentLoadKw;

    /** Operating voltage level, e.g. 132.0 (kV) */
    @Column(name = "voltage_kv")
    private Double voltageKv;

    /** Geographic latitude (decimal degrees) */
    @Column(name = "latitude")
    private Double latitude;

    /** Geographic longitude (decimal degrees) */
    @Column(name = "longitude")
    private Double longitude;

    // ----------------------------------------------------------------
    // Associations
    // ----------------------------------------------------------------

    /** Areas served by this substation */
    @OneToMany(mappedBy = "substation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Areas> areas = new ArrayList<>();

    /** All historical power readings for this substation */
    @OneToMany(mappedBy = "substation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PowerReadings> powerReadings = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ----------------------------------------------------------------
    // Convenience constructor used by SubstationService / seed data
    // ----------------------------------------------------------------
    public Substations(String code, String name, SubProfileType subProfileType,
                       AssetStatus status, Double capacityKw, Double currentLoadKw) {
        this.code           = code;
        this.name           = name;
        this.subProfileType = subProfileType;
        this.status         = status;
        this.capacityKw     = capacityKw;
        this.currentLoadKw  = currentLoadKw;
    }

    // ----------------------------------------------------------------
    // Derived helper
    // ----------------------------------------------------------------
    /** Load factor as a percentage of rated capacity (0–100+). */
    public double loadFactor() {
        if (capacityKw == null || capacityKw == 0 || currentLoadKw == null) return 0.0;
        return (currentLoadKw / capacityKw) * 100.0;
    }
}
