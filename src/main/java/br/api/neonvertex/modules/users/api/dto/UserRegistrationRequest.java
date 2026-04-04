package br.api.neonvertex.modules.users.api.dto;

import jakarta.validation.constraints.*;

public record UserRegistrationRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 11, max = 11) String cpf,
        @NotBlank @Size(min = 8) String password
) {}