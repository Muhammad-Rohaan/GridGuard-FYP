package com.fyp.gridguard.Utilities;

/**
 * Severity level of an {@link com.fyp.gridguard.Models.Alert}.
 *
 * <ul>
 *   <li>{@link #INFO}     – Informational event; no action required.</li>
 *   <li>{@link #WARNING}  – Approaching threshold; monitor closely.</li>
 *   <li>{@link #CRITICAL} – Threshold exceeded; immediate action required.</li>
 * </ul>
 */
public enum AlertSeverity {
    INFO,
    WARNING,
    CRITICAL
}
