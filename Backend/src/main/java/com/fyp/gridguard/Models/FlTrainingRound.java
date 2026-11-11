package com.fyp.gridguard.Models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Tracks the aggregated result of a Federated Learning (FL) training round.
 *
 * <p>GridGuard uses a Federated Learning approach where each substation (or area)
 * acts as a client that trains a local model on its own data.
 * The central server then aggregates local weights into a global model.
 *
 * <p>Each row in this table represents one FL round – useful for:
 * <ul>
 *   <li>Monitoring convergence (global loss / accuracy over rounds)</li>
 *   <li>Auditing which clients participated</li>
 *   <li>Comparing FL global model vs. centralised baseline metrics</li>
 * </ul>
 *
 * <p>Table name: {@code fl_training_rounds}
 */
@Entity
@Table(
    name = "fl_training_rounds",
    indexes = {
        @Index(name = "idx_fl_round_number",    columnList = "round_number"),
        @Index(name = "idx_fl_completed_at",    columnList = "completed_at")
    }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FlTrainingRound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "round_id")
    private Long roundId;

    /** Sequential round index (1, 2, 3 …) */
    @Column(name = "round_number", nullable = false)
    private Integer roundNumber;

    /** Number of FL clients (substations / areas) that participated in this round */
    @Column(name = "participating_clients", nullable = false)
    private Integer participatingClients;

    /** Global model loss after aggregation */
    @Column(name = "global_loss")
    private Double globalLoss;

    /** Global model MAE after aggregation */
    @Column(name = "global_mae")
    private Double globalMae;

    /** Global model RMSE after aggregation */
    @Column(name = "global_rmse")
    private Double globalRmse;

    /** Aggregation strategy used: "FedAvg", "FedProx", etc. */
    @Column(name = "aggregation_strategy", length = 40)
    private String aggregationStrategy;

    /** Path or URI of the saved global model weights for this round */
    @Column(name = "model_weights_path", length = 300)
    private String modelWeightsPath;

    @CreationTimestamp
    @Column(name = "started_at", nullable = false, updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /** Additional metadata stored as JSON (hyperparameters, client IDs, etc.) */
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;
}
