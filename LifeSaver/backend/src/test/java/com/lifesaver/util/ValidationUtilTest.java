package com.lifesaver.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilTest {

    @ParameterizedTest
    @ValueSource(strings = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "a+", "o-"})
    @DisplayName("Should accept all valid standard blood groups")
    void testValidBloodGroups(String bg) {
        assertTrue(ValidationUtil.isValidBloodGroup(bg), "Blood group " + bg + " should be valid");
    }

    @ParameterizedTest
    @ValueSource(strings = {"C+", "XYZ", "A", "O", "123", "", " ", "null"})
    @DisplayName("Should reject invalid blood groups")
    void testInvalidBloodGroups(String bg) {
        assertFalse(ValidationUtil.isValidBloodGroup(bg), "Blood group " + bg + " should be invalid");
    }

    @ParameterizedTest
    @ValueSource(strings = {"user@example.com", "donor.123@hospital.org", "student_cse@college.edu.in"})
    @DisplayName("Should accept valid email formats")
    void testValidEmails(String email) {
        assertTrue(ValidationUtil.isValidEmail(email));
    }

    @ParameterizedTest
    @ValueSource(strings = {"plainaddress", "missing@domain", "@nodomain.com", "space in@mail.com"})
    @DisplayName("Should reject invalid email formats")
    void testInvalidEmails(String email) {
        assertFalse(ValidationUtil.isValidEmail(email));
    }

    @ParameterizedTest
    @ValueSource(strings = {"9876543210", "+91 9876543210", "+91-9876543210", "98765 01234"})
    @DisplayName("Should accept valid 10-digit mobile numbers")
    void testValidPhones(String phone) {
        assertTrue(ValidationUtil.isValidPhone(phone));
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345", "notaphonenumber", "12345678901234567890"})
    @DisplayName("Should reject invalid phone numbers")
    void testInvalidPhones(String phone) {
        assertFalse(ValidationUtil.isValidPhone(phone));
    }

    @Test
    @DisplayName("Should mask donor phone number to protect public privacy")
    void testPhoneMasking() {
        String phone = "+91 9876543210";
        String masked = ValidationUtil.maskPhoneNumber(phone);
        assertNotNull(masked);
        assertTrue(masked.contains("******"), "Masked phone should contain obfuscated middle digits");
    }

    @Test
    @DisplayName("Should mask donor email to protect public privacy while retaining domain")
    void testEmailMasking() {
        String email = "aarav.sharma@demo.mail";
        String masked = ValidationUtil.maskEmail(email);
        assertNotNull(masked);
        assertEquals("aa***a@demo.mail", masked, "Email masking should keep prefix/suffix and domain");

        assertEquals("N/A", ValidationUtil.maskEmail(null));
        assertEquals("N/A", ValidationUtil.maskEmail("   "));
        assertEquals("***@x.com", ValidationUtil.maskEmail("a@x.com"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"+91 98765 01001", "+91-98765-01001", "(987) 654-3210", "987 654 3210"})
    @DisplayName("Should accept valid grouped phone numbers from sample data")
    void testValidGroupedPhones(String phone) {
        assertTrue(ValidationUtil.isValidPhone(phone), "Phone format " + phone + " should be accepted");
    }
}
