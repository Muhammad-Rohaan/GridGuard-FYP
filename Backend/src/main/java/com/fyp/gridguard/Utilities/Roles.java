package com.fyp.gridguard.Utilities;

/**
 * Application-level roles used for RBAC.
 *
 * <ul>
 *   <li>{@link #USER}             – Standard grid observer (read-only dashboard)</li>
 *   <li>{@link #TECHNICAL_WORKER} – Engineer with write access to substations, alerts, etc.</li>
 *   <li>{@link #ADMIN}            – Super-admin; can manage users and all configuration</li>
 * </ul>
 *
 * Spring Security maps these via "ROLE_" prefix automatically,
 * e.g. hasRole("TECHNICAL_WORKER") → authority "ROLE_TECHNICAL_WORKER".
 */
public enum Roles {
    USER,
    TECHNICAL_WORKER,
    ADMIN
}
