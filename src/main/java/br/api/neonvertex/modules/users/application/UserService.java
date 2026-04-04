package br.api.neonvertex.modules.users.application;

import br.api.neonvertex.modules.users.api.dto.UserRegistrationRequest;
import br.api.neonvertex.modules.users.domain.User;
import br.api.neonvertex.modules.users.domain.UserRepository;
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
    public void register(UserRegistrationRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new RuntimeException("Email já cadastrado");
        }
        if (repository.existsByCpf(request.cpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        var user = User.builder()
                .name(request.name())
                .email(request.email())
                .cpf(request.cpf())
                .password(passwordEncoder.encode(request.password()))
                .status("ACTIVE")
                .build();

        repository.save(user);
    }
}