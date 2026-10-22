package com.lifesaver.model;

/**
 * BloodGroup Enum
 * Represents the 8 standard ABO and Rh blood groups.
 * Educational note: Demonstrates Java enum with lookup and validation logic.
 */
public enum BloodGroup {
    A_POSITIVE("A+"),
    A_NEGATIVE("A-"),
    B_POSITIVE("B+"),
    B_NEGATIVE("B-"),
    AB_POSITIVE("AB+"),
    AB_NEGATIVE("AB-"),
    O_POSITIVE("O+"),
    O_NEGATIVE("O-");

    private final String label;

    BloodGroup(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Parse blood group string (e.g. "A+", "O-") safely.
     * @param text input string
     * @return matching BloodGroup or null if invalid
     */
    public static BloodGroup fromString(String text) {
        if (text == null) {
            return null;
        }
        String clean = text.trim().toUpperCase();
        for (BloodGroup bg : BloodGroup.values()) {
            if (bg.label.equalsIgnoreCase(clean) || bg.name().equalsIgnoreCase(clean)) {
                return bg;
            }
        }
        return null;
    }

    public static boolean isValid(String text) {
        return fromString(text) != null;
    }
}
