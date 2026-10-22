package com.lifesaver.model;

/**
 * UrgencyLevel Enum
 * Defines the critical priority for a blood request.
 */
public enum UrgencyLevel {
    NORMAL,
    URGENT,
    CRITICAL;

    public static UrgencyLevel fromString(String text) {
        if (text == null) return NORMAL;
        try {
            return UrgencyLevel.valueOf(text.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return NORMAL;
        }
    }
}
