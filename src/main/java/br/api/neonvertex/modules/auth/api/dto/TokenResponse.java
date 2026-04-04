package br.api.neonvertex.modules.auth.api.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {}