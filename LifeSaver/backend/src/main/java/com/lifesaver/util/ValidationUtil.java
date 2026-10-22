package com.lifesaver.util;

import com.lifesaver.model.BloodGroup;
import com.lifesaver.model.RequestStatus;
import com.lifesaver.model.UrgencyLevel;

import java.util.regex.Pattern;

/**
 * ValidationUtil
 * Common input validation routines for donor registration, updates, and blood requests.
 * Ensures data hygiene before reaching database queries.
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,10}$");

    // Allows 10-digit standard mobile numbers, optionally prefixed with country code (e.g. +91)
    // and supporting standard grouped formats: 9876543210, 98765 01001, (987) 654-3210
    public static final String PHONE_REGEX =
            "^(\\+?\\d{1,4}[-\\s]?)?(\\(?\\d{3}\\)?[-\\s]?\\d{3}[-\\s]?\\d{4}|\\d{5}[-\\s]?\\d{5}|\\d{10})$";

    private static final Pattern PHONE_PATTERN =
            Pattern.compile(PHONE_REGEX);

    private ValidationUtil() {
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return false;
        String trimmed = phone.trim();
        if (PHONE_PATTERN.matcher(trimmed).matches()) {
            return true;
        }
        String clean = trimmed.replaceAll("[\\s\\-\\(\\)]", "");
        return clean.matches("^(\\+?\\d{1,4})?\\d{10}$");
    }

    public static boolean isValidBloodGroup(String bg) {
        return BloodGroup.isValid(bg);
    }

    public static boolean isValidRequestStatus(String status) {
        return RequestStatus.isValid(status);
    }

    public static boolean isValidUrgency(String urgency) {
        if (urgency == null) return false;
        try {
            UrgencyLevel.valueOf(urgency.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static String clean(String text) {
        return text != null ? text.trim() : null;
    }

    /**
     * Masks a contact number for public anonymous search (e.g., +91 98XXX XXXXX or 98765XXXXX)
     * Protects donor privacy from scrapers and anonymous visitors.
     */
    public static String maskPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return "N/A";
        }
        String p = phone.trim();
        if (p.length() <= 4) {
            return "****";
        }
        int keep = Math.min(3, p.length() - 4);
        return p.substring(0, keep) + "******" + p.substring(p.length() - 2);
    }

    /**
     * Masks an email address for public anonymous search (e.g., aa***a@demo.mail)
     * Protects donor privacy while keeping domain clarity.
     */
    public static String maskEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "N/A";
        }
        String e = email.trim();
        int atIndex = e.indexOf('@');
        if (atIndex <= 1) {
            return "***" + (atIndex >= 0 ? e.substring(atIndex) : "");
        }
        String local = e.substring(0, atIndex);
        String domain = e.substring(atIndex);
        if (local.length() <= 2) {
            return local.charAt(0) + "***" + domain;
        }
        return local.substring(0, 2) + "***" + local.substring(local.length() - 1) + domain;
    }
}
