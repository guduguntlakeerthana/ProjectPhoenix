package com.devflowai.service;

import com.devflowai.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;
    private final String secret = "my-super-secret-key-that-is-at-least-256-bits-long-2026-example-key";

    @BeforeEach
    public void setUp() {
        jwtService = new JwtService(secret);
    }

    @Test
    public void testGenerateAndExtractEmail() {
        String email = "keerthana@devflowai.com";
        String token = jwtService.generateToken(email);

        assertNotNull(token);
        String extractedEmail = jwtService.extractEmail(token);
        assertEquals(email, extractedEmail);
    }

    @Test
    public void testTokenValidation() {
        String email = "keerthana@devflowai.com";
        String token = jwtService.generateToken(email);

        assertTrue(jwtService.isTokenValid(token, email));
        assertFalse(jwtService.isTokenValid(token, "other@devflowai.com"));
    }
}
