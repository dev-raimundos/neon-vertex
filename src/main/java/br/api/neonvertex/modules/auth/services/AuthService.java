package br.api.neonvertex.modules.auth.services;

import br.api.neonvertex.modules.auth.dto.LoginRequest;
import br.api.neonvertex.modules.auth.dto.RefreshRequest;
import br.api.neonvertex.modules.auth.dto.TokenResponse;
import br.api.neonvertex.modules.auth.models.RefreshToken;
import br.api.neonvertex.modules.auth.repositories.RefreshTokenRepository;
import br.api.neonvertex.modules.auth.config.JwtProperties;
import br.api.neonvertex.modules.auth.security.UserAuthentication;
import br.api.neonvertex.modules.users.models.User;
import br.api.neonvertex.shared.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    @Transactional
    public TokenResponse login(LoginRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
        );

        // O AuthenticationManager já lança BadCredentialsException se as credenciais
        // forem inválidas — o GlobalExceptionHandler captura via fallback genérico.
        var authentication = authenticationManager.authenticate(authToken);
        var userAuth = (UserAuthentication) authentication.getPrincipal();
        var user = userAuth.getUser();

        refreshTokenRepository.deleteByUser(user);

        var refreshToken = buildRefreshToken(user);
        refreshTokenRepository.save(refreshToken);

        return new TokenResponse(
                tokenService.generateAccessToken(user),
                refreshToken.getToken()
        );
    }

    @Transactional
    public TokenResponse refresh(RefreshRequest request) {
        var refreshToken = refreshTokenRepository
                .findByToken(request.refreshToken())
                .orElseThrow(() -> AppException.unauthorized("Refresh token inválido."));

        // Deleta sempre — expirado ou não — antes de qualquer validação,
        // garantindo que tokens usados nunca permaneçam no banco.
        refreshTokenRepository.delete(refreshToken);

        if (refreshToken.isExpired()) {
            throw AppException.unauthorized("Refresh token expirado.");
        }

        var user = refreshToken.getUser();
        var newRefreshToken = buildRefreshToken(user);
        refreshTokenRepository.save(newRefreshToken);

        return new TokenResponse(
                tokenService.generateAccessToken(user),
                newRefreshToken.getToken()
        );
    }

    // -------------------------------------------------------------------------
    // Internal
    // -------------------------------------------------------------------------

    private RefreshToken buildRefreshToken(User user) {
        return RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(Instant.now().plusMillis(
                        jwtProperties.refreshToken().expirationMs()
                ))
                .build();
    }
}