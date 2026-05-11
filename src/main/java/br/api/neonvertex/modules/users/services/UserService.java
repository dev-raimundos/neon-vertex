package br.api.neonvertex.modules.users.services;

import br.api.neonvertex.core.iam.repositories.RoleRepository;
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

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse register(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw AppException.conflict("E-mail já cadastrado.");
        }
        if (userRepository.existsByCpf(request.cpf())) {
            throw AppException.conflict("CPF já cadastrado.");
        }

        var defaultRole = roleRepository.findByName("USER")
                .orElseThrow(() -> AppException.internalError("Role padrão não encontrada."));

        var user = User.builder()
                .name(request.name())
                .email(request.email())
                .cpf(request.cpf())
                .password(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .status(UserStatus.ACTIVE)
                .build();

        user.addRole(defaultRole);

        return UserResponse.from(userRepository.save(user));
    }
}