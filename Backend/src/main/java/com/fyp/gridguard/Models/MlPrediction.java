package com.fyp.gridguard.Models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Stores the output of the ML forecasting model for a given substation or area.
 *
 * <p>The ML service (Python / FastAPI) posts predictions to the backend REST API
 * which persists them here. The dashboard can then display predicted vs. actual load.
 *
 * <p>Supported model types (stored as plain strings):
 * <ul>
 *   <li>"LSTM"       – Long Short-Term Memory (time-series forecasting)</li>
 *   <li>"XGBoost"    – Gradient-boosted tree model</li>
 *   <li>"FL_GLOBAL"  – Federated Learning global model</li>
 *   <li>"ENSEMBLE"   – Blended prediction from multiple models</li>
 * </ul>
 *
 * <p>Table name: {@code ml_predictions}
 */
@Entity
@Table(
    name = "ml_predictions",
    indexes = {
        @Index(name = "idx_ml_predictions_substation_id",   columnList = "substation_id"),
        @Index(name = "idx_ml_predictions_predicted_for",   columnList = "predicted_for"),
        @Index(name = "idx_ml_predictions_model_type",      columnList = "model_type")
    }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "substation")
public class MlPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prediction_id")
    private Long predictionId;

    /** The substation or area for which the prediction was made */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "substation_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_ml_predictions_substation"))
    private Substations substation;

    /** Area (optional – null if the prediction is at substation level) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id",
                foreignKey = @ForeignKey(name = "fk_ml_predictions_area"))
    private Areas area;

    /**
     * The future simulation timestamp this prediction is for.
     * E.g. if the prediction was made at T, this may be T + 15 min.
     */
    @Column(name = "predicted_for", nullable = false)
    private LocalDateTime predictedFor;

    /** The wall-clock (or sim-clock) time when the prediction was generated */
    @CreationTimestamp
    @Column(name = "predicted_at", nullable = false, updatable = false)
    private LocalDateTime predictedAt;

    /** Name / version of the model that produced this prediction */
    @Column(name = "model_type", nullable = false, length = 50)
    private String modelType;

    /** Predicted power consumption (kW) */
    @Column(name = "predicted_load_kw", nullable = false)
    private Double predictedLoadKw;

    /** Actual observed load at the predicted time (filled in after the fact) */
    @Column(name = "actual_load_kw")
    private Double actualLoadKw;

    /** Confidence score of the prediction (0.0 – 1.0), if the model provides it */
    @Column(name = "confidence_score")
    private Double confidenceScore;

    /** Mean Absolute Error between predicted and actual (computed during evaluation) */
    @Column(name = "mae")
    private Double mae;

    /** Root Mean Squared Error */
    @Column(name = "rmse")
    private Double rmse;

    /** Whether this prediction was flagged as anomalous by the XAI / anomaly layer */
    @Column(name = "is_anomaly", nullable = false)
    private boolean isAnomaly = false;
}
