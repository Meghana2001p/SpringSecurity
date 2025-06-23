package com.example.jwtsecurity.util;

import java.security.SecureRandom;
import java.util.Base64;

public class SecretKeyGenerator {
    public static String generateSecureToken(int byteLength) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[byteLength];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getEncoder().encodeToString(tokenBytes); // 👈 change this
    }
}
