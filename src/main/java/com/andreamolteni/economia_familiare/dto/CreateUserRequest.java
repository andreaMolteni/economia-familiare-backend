package com.andreamolteni.economia_familiare.dto;


import jakarta.validation.constraints.*;

public record CreateUserRequest(
        @NotBlank String nome,
        @NotBlank String cognome,
        @Email @NotBlank String email,
        @NotBlank String userName,
        @NotBlank String password,
        @Min(0) int tipoUtente
) {}
