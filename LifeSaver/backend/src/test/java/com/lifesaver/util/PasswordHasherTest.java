package com.lifesaver.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {

    @Test
    @DisplayName("Should generate 32-character hex salt")
    void testSaltGeneration() {
        String salt = PasswordHasher.generateSalt();
        assertNotNull(salt);
        assertEquals(32, salt.length(), "Salt should be 16 bytes (32 hex chars)");

        String salt2 = PasswordHasher.generateSalt();
        assertNotEquals(salt, salt2, "Subsequent salts must be randomly unique");
    }

    @Test
    @DisplayName("Should correctly hash and verify valid password")
    void testHashAndVerifyValidPassword() {
        String password = "StrongStudentPassword#2026";
        String salt = PasswordHasher.generateSalt();

        String hash = PasswordHasher.hashPassword(password, salt);
        assertNotNull(hash);
        assertEquals(64, hash.length(), "SHA-256 hash should be 32 bytes (64 hex chars)");

        boolean matches = PasswordHasher.verifyPassword(password, hash, salt);
        assertTrue(matches, "Password verification should return true for matching password");
    }

    @Test
    @DisplayName("Should reject incorrect password")
    void testVerifyWrongPassword() {
        String password = "CorrectPassword123";
        String wrongPassword = "WrongPassword123";
        String salt = PasswordHasher.generateSalt();

        String hash = PasswordHasher.hashPassword(password, salt);
        boolean matches = PasswordHasher.verifyPassword(wrongPassword, hash, salt);
        assertFalse(matches, "Password verification must reject incorrect password");
    }

    @Test
    @DisplayName("Different salts must produce different hashes for identical password")
    void testSaltEffectOnHash() {
        String password = "SamePassword123";
        String salt1 = PasswordHasher.generateSalt();
        String salt2 = PasswordHasher.generateSalt();

        String hash1 = PasswordHasher.hashPassword(password, salt1);
        String hash2 = PasswordHasher.hashPassword(password, salt2);

        assertNotEquals(hash1, hash2, "Hashes with distinct salts must never match (Rainbow table prevention)");
    }

    @Test
    @DisplayName("Verify sample_data.sql admin credentials match AdminPassword@123")
    void testSampleDataAdminCredentials() {
        String password = "AdminPassword@123";
        String salt = "db7cae2b68e37fec58a68d1cbe509633";
        String hash = "b0ab6c63d5f30863aaf0d4c5ce4cce6787dd857acefcd60c4d8340f94670baf1";
        assertTrue(PasswordHasher.verifyPassword(password, hash, salt), "sample_data.sql hash must match default password");
    }
}
