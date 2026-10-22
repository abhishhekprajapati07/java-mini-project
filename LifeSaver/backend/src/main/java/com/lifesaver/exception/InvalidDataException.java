package com.lifesaver.exception;

/**
 * Thrown when business validation fails (e.g., invalid blood group, bad phone number).
 * Maps to HTTP 400 BAD REQUEST.
 */
public class InvalidDataException extends RuntimeException {

    public InvalidDataException(String message) {
        super(message);
    }
}
