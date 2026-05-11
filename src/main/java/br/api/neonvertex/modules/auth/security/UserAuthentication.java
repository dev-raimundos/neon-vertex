package br.api.neonvertex.modules.auth.security;

import br.api.neonvertex.modules.users.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record UserAuthentication(User user) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Admin tem bypass total — uma authority especial que o Spring Security
        // reconhece via hasRole('ADMIN') sem precisar de permissões cadastradas
        if (user.isAdmin()) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        // Demais usuários recebem as permissões achatadas de todas as suas roles
        return user.getRoles().stream()
                .flatMap(role -> Stream.concat(
                        // inclui a própria role (ex: ROLE_USER)
                        Stream.of(new SimpleGrantedAuthority("ROLE_" + role.getName())),
                        // inclui cada permissão da role (ex: users.profile.read)
                        role.getPermissions().stream()
                                .map(p -> new SimpleGrantedAuthority(p.getName()))
                ))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }
}