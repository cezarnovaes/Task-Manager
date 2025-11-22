package com.cezar.taskapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.cezar.taskapi.dto.AuthResponse;
import com.cezar.taskapi.dto.LoginRequest;
import com.cezar.taskapi.dto.RegisterRequest;
import com.cezar.taskapi.dto.UserResponse;
import com.cezar.taskapi.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AuthController Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Teste Usuario");
        registerRequest.setEmail("teste@email.com");
        registerRequest.setPassword("senha123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("teste@email.com");
        loginRequest.setPassword("senha123");

        UserResponse userResponse = new UserResponse(
                1L, "Teste Usuario", "teste@email.com", LocalDateTime.now()
        );
        authResponse = new AuthResponse("jwt-token-mock", userResponse);
    }

    @Test
    @DisplayName("POST /api/auth/register - Deve registrar usuario com sucesso")
    void shouldRegisterUserSuccessfully() throws Exception {
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-token-mock"))
                .andExpect(jsonPath("$.user.email").value("teste@email.com"));
    }

    @Test
    @DisplayName("POST /api/auth/register - Deve falhar com dados invalidos")
    void shouldFailRegistrationWithInvalidData() throws Exception {
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setName("AB"); // Muito curto
        invalidRequest.setEmail("email-invalido");
        invalidRequest.setPassword("123"); // Muito curta

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login - Deve fazer login com sucesso")
    void shouldLoginSuccessfully() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-mock"))
                .andExpect(jsonPath("$.type").value("Bearer"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Deve falhar com email invalido")
    void shouldFailLoginWithInvalidEmail() throws Exception {
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setEmail("email-invalido");
        invalidRequest.setPassword("senha123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}