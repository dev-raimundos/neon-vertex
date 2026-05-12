package br.api.neonvertex.modules.auth.repositories;

import java.util.Optional;
import java.util.UUID;

import br.api.neonvertex.modules.auth.models.RefreshToken;

import org.springframework.data.jpa.repository.JpaRepository;

import br.api.neonvertex.modules.users.models.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);
}
