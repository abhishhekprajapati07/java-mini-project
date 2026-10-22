package com.lifesaver.exception;

/**
 * Thrown when attempting to create a resource with a unique constraint violation
 * (e.g., duplicate donor email or admin username).
 * Maps to HTTP 409 CONFLICT.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
