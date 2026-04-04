package br.api.neonvertex.modules.auth.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,
        AccessToken accessToken,
        RefreshToken refreshToken
) {
    public record AccessToken(long expirationMs) {}
    public record RefreshToken(long expirationMs) {}
}