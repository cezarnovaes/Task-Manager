package com.cezar.taskapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados para login")
public class LoginRequest {

    @Schema(description = "Email do usuario", example = "joao@email.com")
    @NotBlank(message = "Email e obrigatorio")
    @Email(message = "Email invalido")
    private String email;

    @Schema(description = "Senha do usuario", example = "senha123")
    @NotBlank(message = "Senha e obrigatoria")
    private String password;

    // Getters e Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}