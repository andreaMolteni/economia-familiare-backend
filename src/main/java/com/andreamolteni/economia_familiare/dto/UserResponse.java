package com.andreamolteni.economia_familiare.dto;

public record UserResponse(
        Long id,
        String nome,
        String cognome,
        String email,
        String userName,
        int tipoUtente
) {}
