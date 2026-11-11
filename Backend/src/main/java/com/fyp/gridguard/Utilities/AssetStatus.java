package com.fyp.gridguard.Utilities;

/**
 * Operational health status of a substation or area.
 *
 * <ul>
 *   <li>{@link #HEALTHY}  – Load is within normal operating range.</li>
 *   <li>{@link #WARNING}  – Load is approaching capacity (80–95 % threshold).</li>
 *   <li>{@link #CRITICAL} – Load has exceeded safe capacity (> 95 %).</li>
 *   <li>{@link #OFFLINE}  – Asset is shut down or unreachable.</li>
 *   <li>{@link #UNKNOWN}  – Status has not yet been determined.</li>
 * </ul>
 */
public enum AssetStatus {
    HEALTHY,
    WARNING,
    CRITICAL,
    OFFLINE,
    UNKNOWN
}
