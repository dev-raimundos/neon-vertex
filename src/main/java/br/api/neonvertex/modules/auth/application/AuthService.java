package br.api.neonvertex.modules.auth.application;

import br.api.neonvertex.modules.auth.api.dto.LoginRequest;
import br.api.neonvertex.modules.auth.api.dto.RefreshRequest;
import br.api.neonvertex.modules.auth.api.dto.TokenResponse;
import br.api.neonvertex.modules.auth.domain.RefreshToken;
import br.api.neonvertex.modules.auth.domain.RefreshTokenRepository;
import br.api.neonvertex.modules.auth.infrastructure.JwtProperties;
import br.api.neonvertex.modules.auth.infrastructure.TokenService;
import br.api.neonvertex.modules.auth.infrastructure.UserAuthentication;
import br.api.neonvertex.modules.users.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

        Authentication authentication = authenticationManager.authenticate(
                authToken
        );

        UserAuthentication userAuth = (UserAuthentication) authentication.getPrincipal();

        User user = userAuth.getUser();

        refreshTokenRepository.deleteByUser(user);

        String accessToken = tokenService.generateAccessToken(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(Instant.now().plusMillis(
                        jwtProperties.refreshToken().expirationMs()
                ))
                .build();

        refreshTokenRepository.save(refreshToken);

        return new TokenResponse(accessToken, refreshToken.getToken());
    }

    @Transactional
    public TokenResponse refresh(RefreshRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(
                request.refreshToken()
        ).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Refresh token inválido"
                )
        );

        if (refreshToken.isExpired()) {

            refreshTokenRepository.delete(refreshToken);

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Refresh token expirado"
            );
        }

        User user = refreshToken.getUser();
        String newAccessToken = tokenService.generateAccessToken(user);

        refreshTokenRepository.delete(refreshToken);

        RefreshToken newRefreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(Instant.now().plusMillis(
                        jwtProperties.refreshToken().expirationMs()
                ))
                .build();
        refreshTokenRepository.save(newRefreshToken);

        return new TokenResponse(
                newAccessToken,
                newRefreshToken.getToken()
        );
    }
}