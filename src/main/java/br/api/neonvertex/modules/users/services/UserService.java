package br.api.neonvertex.modules.users.services;

import br.api.neonvertex.core.iam.repositories.RoleRepository;
import br.api.neonvertex.modules.users.dto.UserRegistrationRequest;
import br.api.neonvertex.modules.users.dto.UserResponse;
import br.api.neonvertex.modules.users.enums.UserStatus;
import br.api.neonvertex.modules.users.models.User;
import br.api.neonvertex.modules.users.repositories.UserRepository;
import br.api.neonvertex.shared.exception.AppException;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String EMAIL_ALREADY_EXISTS = "O e-mail informado já está cadastrado ou está inativo.";
    private static final String CPF_ALREADY_EXISTS = "O CPF informado já foi cadastrado ou está inativo.";
    private static final String ROLE_NOT_FOUND = "O cargo padrão não foi encontrado.";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse register(UserRegistrationRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw AppException.conflict(EMAIL_ALREADY_EXISTS);
        }

        if (userRepository.existsByCpf(request.cpf())) {
            throw AppException.conflict(CPF_ALREADY_EXISTS);
        }

        var defaultRole = roleRepository.findByName("USER")
            .orElseThrow(() -> AppException.internalError(ROLE_NOT_FOUND));

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

    public Page<UserResponse> listAll(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size)).map(UserResponse::from);
    }
}
