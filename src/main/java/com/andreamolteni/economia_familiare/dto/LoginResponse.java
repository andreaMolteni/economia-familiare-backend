package com.andreamolteni.economia_familiare.dto;

public record LoginResponse(String accessToken, String tokenType, long expiresInSeconds) {}

