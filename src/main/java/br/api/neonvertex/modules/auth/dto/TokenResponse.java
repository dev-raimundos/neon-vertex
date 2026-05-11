package br.api.neonvertex.modules.auth.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {}