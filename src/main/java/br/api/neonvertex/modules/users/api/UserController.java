package br.api.neonvertex.modules.users.api;

import br.api.neonvertex.modules.users.api.dto.UserRegistrationRequest;
import br.api.neonvertex.modules.users.application.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody @Valid UserRegistrationRequest request) {
        service.register(request);
    }
}