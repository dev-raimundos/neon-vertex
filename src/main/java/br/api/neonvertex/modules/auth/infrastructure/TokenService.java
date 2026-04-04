package br.api.neonvertex.modules.auth.infrastructure;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import br.api.neonvertex.modules.users.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtProperties jwtProperties;

    public String generateAccessToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(jwtProperties.secret());
        return JWT.create()
                .withIssuer("neon-vertex")
                .withSubject(user.getEmail())
                .withClaim("id", user.getId().toString())
                .withExpiresAt(Instant.now().plusMillis(jwtProperties.accessToken().expirationMs()))
                .sign(algorithm);
    }

    public String validateTokenAndGetSubject(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtProperties.secret());
            return JWT.require(algorithm)
                    .withIssuer("neon-vertex")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return null;
        }
    }
}