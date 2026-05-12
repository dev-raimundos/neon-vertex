package br.api.neonvertex.modules.auth.services;

import br.api.neonvertex.modules.auth.config.JwtProperties;
import br.api.neonvertex.modules.auth.dto.LoginRequest;
import br.api.neonvertex.modules.auth.models.RefreshToken;
import br.api.neonvertex.modules.auth.repositories.RefreshTokenRepository;
import br.api.neonvertex.modules.auth.security.UserAuthentication;
import br.api.neonvertex.modules.users.models.User;
import br.api.neonvertex.modules.users.repositories.UserRepository;
import br.api.neonvertex.shared.exception.AppException;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String COOKIE_ACCESS_TOKEN = "access_token";
    private static final String COOKIE_REFRESH_TOKEN = "refresh_token";
    private static final String ERR_USER_INACTIVE = "Usuário inativo.";
    private static final String ERR_INVALID_CREDENTIALS = "Usuário não encontrado ou senha incorreta.";
    private static final String ERR_INVALID_REFRESH_TOKEN = "Refresh token inválido ou ausente.";
    private static final String ERR_EXPIRED_REFRESH_TOKEN = "Refresh token expirado.";

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;

    @Transactional
    public void login(LoginRequest request, HttpServletResponse response) {
        var authToken = new UsernamePasswordAuthenticationToken(request.email(), request.password());

        try {
            var authentication = authenticationManager.authenticate(authToken);
            var userAuth = (UserAuthentication) authentication.getPrincipal();
            var user = userAuth.user();

            user.recordLogin();
            userRepository.save(user);

            refreshTokenRepository.deleteByUser(user);

            var refreshToken = buildRefreshToken(user);
            refreshTokenRepository.save(refreshToken);

            setAccessTokenCookie(response, tokenService.generateAccessToken(user));
            setRefreshTokenCookie(response, refreshToken.getToken());

        } catch (DisabledException ex) {
            throw AppException.unauthorized(ERR_USER_INACTIVE);
        } catch (BadCredentialsException ex) {
            throw AppException.unauthorized(ERR_INVALID_CREDENTIALS);
        }
    }

    @Transactional
    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        var refreshTokenValue = extractCookie(request, COOKIE_REFRESH_TOKEN);

        var refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
            .orElseThrow(() -> AppException.unauthorized(ERR_INVALID_REFRESH_TOKEN));

        refreshTokenRepository.delete(refreshToken);

        if (refreshToken.isExpired()) {
            clearCookies(response);
            throw AppException.unauthorized(ERR_EXPIRED_REFRESH_TOKEN);
        }

        var user = refreshToken.getUser();
        var newRefreshToken = buildRefreshToken(user);
        refreshTokenRepository.save(newRefreshToken);

        setAccessTokenCookie(response, tokenService.generateAccessToken(user));
        setRefreshTokenCookie(response, newRefreshToken.getToken());
    }

    public void logout(HttpServletResponse response) {
        clearCookies(response);
    }

    // -------------------------------------------------------------------------
    // Internal
    // -------------------------------------------------------------------------

    private RefreshToken buildRefreshToken(User user) {
        return RefreshToken.builder()
            .token(UUID.randomUUID().toString())
            .user(user)
            .expiryDate(Instant.now().plusMillis(jwtProperties.refreshToken().expirationMs()))
            .build();
    }

    private String extractCookie(HttpServletRequest request, String name) {
        var cookie = WebUtils.getCookie(request, name);
        if (cookie == null) {
            throw AppException.unauthorized(ERR_INVALID_REFRESH_TOKEN);
        }
        return cookie.getValue();
    }

    private void setAccessTokenCookie(HttpServletResponse response, String token) {
        var cookie = new Cookie(COOKIE_ACCESS_TOKEN, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtProperties.accessToken().expirationMs() / 1000));
        response.addCookie(cookie);
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String token) {
        var cookie = new Cookie(COOKIE_REFRESH_TOKEN, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/api/auth/refresh");
        cookie.setMaxAge((int) (jwtProperties.refreshToken().expirationMs() / 1000));
        response.addCookie(cookie);
    }

    private void clearCookies(HttpServletResponse response) {
        var accessCookie = new Cookie(COOKIE_ACCESS_TOKEN, "");
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);
        response.addCookie(accessCookie);

        var refreshCookie = new Cookie(COOKIE_REFRESH_TOKEN, "");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/api/auth/refresh");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);
    }
}
