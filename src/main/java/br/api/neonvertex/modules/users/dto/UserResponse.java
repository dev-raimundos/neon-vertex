package br.api.neonvertex.modules.users.dto;

import br.api.neonvertex.core.iam.models.Role;
import br.api.neonvertex.modules.users.enums.UserStatus;
import br.api.neonvertex.modules.users.models.User;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record UserResponse(
    UUID id,
    String name,
    String email,
    String phone,
    String avatarUrl,
    UserStatus status,
    boolean emailVerified,
    Set<String> roles,
    OffsetDateTime lastLoginAt,
    OffsetDateTime createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPhone(),
            user.getAvatarUrl(),
            user.getStatus(),
            user.isEmailVerified(),
            user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet()),
            user.getLastLoginAt(),
            user.getCreatedAt()
        );
    }
}
