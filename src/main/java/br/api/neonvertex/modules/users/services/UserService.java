package br.api.neonvertex.modules.users.services;

import br.api.neonvertex.modules.users.dto.UserRegistrationRequest;
import br.api.neonvertex.modules.users.dto.UserResponse;
import br.api.neonvertex.modules.users.models.User;
import br.api.neonvertex.modules.users.enums.UserStatus;
import br.api.neonvertex.modules.users.repositories.UserRepository;
import br.api.neonvertex.shared.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse register(UserRegistrationRequest request) {

        if (repository.existsByEmail(request.email())) {
            throw AppException.conflict("E-mail já cadastrado.");
        }

        if (repository.existsByCpf(request.cpf())) {
            throw AppException.conflict("CPF já cadastrado.");
        }

        var user = User.builder()
                .name(request.name())
                .email(request.email())
                .cpf(request.cpf())
                .password(passwordEncoder.encode(request.password()))
                .status(UserStatus.ACTIVE)
                .build();

        return UserResponse.from(repository.save(user));
    }
}