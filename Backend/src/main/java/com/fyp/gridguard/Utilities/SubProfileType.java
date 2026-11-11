package com.fyp.gridguard.Utilities;

/**
 * Load-profile type of a substation or area.
 *
 * Used to select the correct CSV dataset column and ML forecasting model.
 */
public enum SubProfileType {
    RESIDENTIAL,
    COMMERCIAL,
    INDUSTRIAL,
    MIXED
}
