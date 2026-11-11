package com.fyp.gridguard.Models;

import com.fyp.gridguard.Utilities.AssetStatus;
import com.fyp.gridguard.Utilities.SubProfileType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a geographic or administrative area fed by a {@link Substations}.
 *
 * <p>Each area has a specific load profile type (RESIDENTIAL, COMMERCIAL, etc.)
 * and stores its current real-time load. The TickEngine updates
 * {@link #currentLoadKw} and {@link #updatedAt} every simulation tick.
 *
 * <p>An area can have many {@link LoadReading} dataset records (one per 5-min slot).
 */
@Entity
@Table(
    name = "areas",
    uniqueConstraints = @UniqueConstraint(name = "uq_areas_code", columnNames = "code")
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"substation", "loadReadings"})
public class Areas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Human-readable name, e.g. "Residential Area 1" */
    @Column(nullable = false, length = 120)
    private String name;

    /** Short unique code, e.g. "AREA-R1" */
    @Column(nullable = false, unique = true, length = 30)
    private String code;

    /** Load profile type for this area */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SubProfileType type;

    /** Operational health driven by load factor */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssetStatus status = AssetStatus.UNKNOWN;

    /** Maximum load capacity this area can sustain (kW) */
    @Column(name = "capacity_kw")
    private Double capacityKw;

    /** Real-time current load (kW); updated every TickEngine cycle */
    @Column(name = "current_load_kw")
    private Double currentLoadKw;

    // ----------------------------------------------------------------
    // Associations
    // ----------------------------------------------------------------

    /** The substation supplying power to this area */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "substation_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_areas_substation"))
    private Substations substation;

    /** All historical dataset readings for this area */
    @OneToMany(mappedBy = "area", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LoadReading> loadReadings = new ArrayList<>();

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
