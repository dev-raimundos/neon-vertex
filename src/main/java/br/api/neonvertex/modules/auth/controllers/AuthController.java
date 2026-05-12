package br.api.neonvertex.modules.auth.controllers;

import br.api.neonvertex.modules.auth.dto.LoginRequest;
import br.api.neonvertex.modules.auth.services.AuthService;
import br.api.neonvertex.shared.response.AppResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    public ResponseEntity<AppResponse.Body<Void>> login(
        @RequestBody @Valid LoginRequest request,
        HttpServletResponse response) {
        authService.login(request, response);
        return AppResponse.ok(null, "Bem-vindo!");
    }

    @PostMapping("/refresh")
    public ResponseEntity<AppResponse.Body<Void>> refresh(
        HttpServletRequest request,
        HttpServletResponse response) {
        authService.refresh(request, response);
        return AppResponse.ok(null);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        authService.logout(response);
        return AppResponse.noContent();
    }
}
