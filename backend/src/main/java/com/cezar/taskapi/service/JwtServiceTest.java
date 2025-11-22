package com.cezar.taskapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtService Tests")
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Injeta valores que normalmente vem do application.properties
        ReflectionTestUtils.setField(jwtService, "secret", 
            "chave-secreta-teste-que-precisa-ter-pelo-menos-256-bits-para-funcionar-2024");
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L);
    }

    @Test
    @DisplayName("Deve gerar token valido")
    void shouldGenerateValidToken() {
        String email = "teste@email.com";
        
        String token = jwtService.generateToken(email);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT tem 3 partes
    }

    @Test
    @DisplayName("Deve extrair email do token")
    void shouldExtractEmailFromToken() {
        String email = "teste@email.com";
        String token = jwtService.generateToken(email);
        
        String extractedEmail = jwtService.extractEmail(token);
        
        assertEquals(email, extractedEmail);
    }

    @Test
    @DisplayName("Deve validar token correto")
    void shouldValidateCorrectToken() {
        String email = "teste@email.com";
        String token = jwtService.generateToken(email);
        
        Boolean isValid = jwtService.validateToken(token, email);
        
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Deve rejeitar token com email diferente")
    void shouldRejectTokenWithDifferentEmail() {
        String email = "teste@email.com";
        String token = jwtService.generateToken(email);
        
        Boolean isValid = jwtService.validateToken(token, "outro@email.com");
        
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve extrair data de expiracao")
    void shouldExtractExpiration() {
        String token = jwtService.generateToken("teste@email.com");
        
        var expiration = jwtService.extractExpiration(token);
        
        assertNotNull(expiration);
        assertTrue(expiration.getTime() > System.currentTimeMillis());
    }
}