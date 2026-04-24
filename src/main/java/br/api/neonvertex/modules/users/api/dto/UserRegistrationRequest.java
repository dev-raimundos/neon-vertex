package br.api.neonvertex.modules.users.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(
        @NotBlank
        String name,
        @NotBlank
        @Email
        String email,
        @NotBlank
        @Size(min = 11, max = 11)
        String cpf,
        @NotBlank
        @Size(min = 8)
        String password
        ) {

}
