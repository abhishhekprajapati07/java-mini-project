package com.lifesaver.model;

/**
 * RequestStatus Enum
 * Tracks the workflow lifecycle of a blood request.
 * Workflow: PENDING -> VERIFIED -> FULFILLED (or REJECTED)
 */
public enum RequestStatus {
    PENDING,
    VERIFIED,
    FULFILLED,
    REJECTED;

    public static RequestStatus fromString(String text) {
        if (text == null) return null;
        try {
            return RequestStatus.valueOf(text.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static boolean isValid(String text) {
        return fromString(text) != null;
    }
}
