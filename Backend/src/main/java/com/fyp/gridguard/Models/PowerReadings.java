package com.fyp.gridguard.Models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Live / near-real-time power measurement snapshot for a {@link Substations}.
 *
 * <p>These rows are written by the TickEngine on every simulation tick (every 5 s)
 * and represent the electrical state of the substation at that instant.
 * They feed the live dashboard panel and historical chart queries.
 *
 * <p>Table name: {@code power_readings}
 */
@Entity
@Table(
    name = "power_readings",
    indexes = {
        @Index(name = "idx_power_readings_substation_id", columnList = "substation_id"),
        @Index(name = "idx_power_readings_timestamp",     columnList = "timestamp")
    }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "substation")
public class PowerReadings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reading_id")
    private Long readingId;

    /** Substation this reading belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "substation_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_power_readings_substation"))
    private Substations substation;

    /** Total active power consumption at this instant (kW) */
    @Column(name = "power_consumption_kw", nullable = false)
    private Double powerConsumptionKw;

    /** Bus voltage at the substation (kV) */
    @Column(name = "voltage_kv")
    private Double voltageKv;

    /** Line current (Amperes) */
    @Column(name = "current_ampere")
    private Double currentAmpere;

    /** Power factor (0.0 – 1.0) */
    @Column(name = "power_factor")
    private Double powerFactor;

    /** Reactive power (kVAR) */
    @Column(name = "reactive_power_kvar")
    private Double reactivePowerKvar;

    /** Apparent power (kVA) */
    @Column(name = "apparent_power_kva")
    private Double apparentPowerKva;

    /** Frequency of the supply (Hz), typically 50 or 60 */
    @Column(name = "frequency_hz")
    private Double frequencyHz;

    /**
     * Wall-clock timestamp when the reading was captured.
     * For simulation runs this is the simulated dataset time.
     */
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}
