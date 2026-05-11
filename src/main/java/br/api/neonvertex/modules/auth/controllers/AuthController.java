package br.api.neonvertex.modules.auth.controllers;

import br.api.neonvertex.modules.auth.dto.LoginRequest;
import br.api.neonvertex.modules.auth.dto.RefreshRequest;
import br.api.neonvertex.modules.auth.dto.TokenResponse;
import br.api.neonvertex.modules.auth.services.AuthService;
import br.api.neonvertex.shared.response.AppResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AppResponse.Envelope<TokenResponse>> login(
            @RequestBody @Valid LoginRequest request) {
        return AppResponse.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AppResponse.Envelope<TokenResponse>> refresh(
            @RequestBody @Valid RefreshRequest request) {
        return AppResponse.ok(authService.refresh(request));
    }
}