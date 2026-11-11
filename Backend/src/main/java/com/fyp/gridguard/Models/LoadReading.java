package com.fyp.gridguard.Models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Historical load reading for a specific {@link Areas}.
 *
 * <p>Rows are bulk-imported from the residential CSV dataset
 * (5-minute intervals) via {@code importDataset.py} or the Java
 * {@code DatasetImportRunner}.
 *
 * <p>The TickEngine queries this table by {@code (area_id, recorded_at)}
 * to replay historical demand in the simulation clock.
 *
 * <p>Table name: {@code load_readings}
 */
@Entity
@Table(
    name = "load_readings",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_load_readings_area_time",
        columnNames = {"area_id", "recorded_at"}
    ),
    indexes = {
        @Index(name = "idx_load_readings_area_id",    columnList = "area_id"),
        @Index(name = "idx_load_readings_recorded_at", columnList = "recorded_at")
    }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "area")
public class LoadReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** The area this reading belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_load_readings_area"))
    private Areas area;

    /**
     * Dataset timestamp at 5-minute resolution, e.g. 2017-01-01T00:00:00.
     * This is the simulated time, not wall-clock time.
     */
    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    /** Power consumption value in kilowatts */
    @Column(name = "load_kw", nullable = false)
    private Double loadKw;
}
