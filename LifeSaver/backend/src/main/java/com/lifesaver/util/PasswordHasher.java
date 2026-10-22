package com.lifesaver.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.HexFormat;

/**
 * PasswordHasher Utility
 * Uses Java's built-in cryptographic security API: PBKDF2WithHmacSHA256 with random salt.
 * 
 * Educational concepts demonstrated:
 * 1. Cryptographic Salting: Protects against precomputed Rainbow Table attacks.
 * 2. Key Stretching (65,536 iterations): Slows down brute-force and dictionary attacks.
 * 3. Constant-Time Verification: Prevents side-channel timing attacks using MessageDigest.isEqual.
 * 4. Zero external libraries: 100% standard Java SE security API.
 */
public final class PasswordHasher {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256; // 32 bytes
    private static final int SALT_BYTES = 16;  // 128-bit salt

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private PasswordHasher() {
        // Private constructor prevents instantiation
    }

    /**
     * Generate a cryptographically secure 16-byte random salt.
     * @return Hex-encoded salt string (32 characters)
     */
    public static String generateSalt() {
        byte[] salt = new byte[SALT_BYTES];
        SECURE_RANDOM.nextBytes(salt);
        return HexFormat.of().formatHex(salt);
    }

    /**
     * Hash a plain-text password using the given hex-encoded salt.
     * @param plainPassword user-supplied plaintext password
     * @param saltHex hex string of the salt
     * @return Hex-encoded password hash (64 characters)
     */
    public static String hashPassword(String plainPassword, String saltHex) {
        if (plainPassword == null || saltHex == null) {
            throw new IllegalArgumentException("Password and salt must not be null");
        }
        try {
            byte[] salt = HexFormat.of().parseHex(saltHex);
            PBEKeySpec spec = new PBEKeySpec(plainPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Cryptographic algorithm unavailable", e);
        }
    }

    /**
     * Verify whether a candidate plaintext password matches the stored hash and salt.
     * Uses constant-time equality check to prevent timing attacks.
     * @param candidatePassword plaintext password to test
     * @param storedHash expected hex hash
     * @param storedSaltHex hex salt used during registration
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String candidatePassword, String storedHash, String storedSaltHex) {
        if (candidatePassword == null || storedHash == null || storedSaltHex == null) {
            return false;
        }
        String calculatedHash = hashPassword(candidatePassword, storedSaltHex);
        // Constant-time byte array comparison
        return MessageDigest.isEqual(
                calculatedHash.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                storedHash.getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );
    }

    // Command-line helper to generate a hash for initial database seeding
    public static void main(String[] args) {
        String testPassword = args.length > 0 ? args[0] : "AdminPassword@123";
        String salt = generateSalt();
        String hash = hashPassword(testPassword, salt);
        System.out.println("Password: " + testPassword);
        System.out.println("Salt:     " + salt);
        System.out.println("Hash:     " + hash);
    }
}
