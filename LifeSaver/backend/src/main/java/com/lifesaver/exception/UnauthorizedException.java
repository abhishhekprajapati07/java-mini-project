package com.lifesaver.exception;

/**
 * Thrown when an unauthenticated or unauthorized user accesses protected administrative operations.
 * Maps to HTTP 401 UNAUTHORIZED.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
