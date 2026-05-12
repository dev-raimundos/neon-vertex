package br.api.neonvertex.modules.users.controllers;

import br.api.neonvertex.modules.users.dto.UserRegistrationRequest;
import br.api.neonvertex.modules.users.dto.UserResponse;
import br.api.neonvertex.modules.users.services.UserService;
import br.api.neonvertex.shared.response.AppResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping("/register")
    public ResponseEntity<AppResponse.Body<UserResponse>> register(@RequestBody @Valid UserRegistrationRequest request) {
        return AppResponse.created(
            service.register(request),
            "Usuário cadastrado com sucesso!"
        );
    }

    @GetMapping
    public ResponseEntity<AppResponse.PageBody<UserResponse>> listAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return AppResponse.paginated(service.listAll(page, size));
    }
}
