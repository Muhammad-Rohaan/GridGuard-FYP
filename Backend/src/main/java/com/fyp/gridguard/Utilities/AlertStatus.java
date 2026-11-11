package com.fyp.gridguard.Utilities;

/**
 * Resolution state of an {@link com.fyp.gridguard.Models.Alert}.
 *
 * <ul>
 *   <li>{@link #OPEN}         – Alert is active and unacknowledged.</li>
 *   <li>{@link #ACKNOWLEDGED} – A technical worker has seen it; still active.</li>
 *   <li>{@link #RESOLVED}     – The underlying condition has cleared.</li>
 * </ul>
 */
public enum AlertStatus {
    OPEN,
    ACKNOWLEDGED,
    RESOLVED
}
