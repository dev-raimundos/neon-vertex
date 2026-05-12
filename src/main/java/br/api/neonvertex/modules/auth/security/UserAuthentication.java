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
        if (user.isAdmin()) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return user.getRoles().stream().flatMap(role -> {
            var roleAuthority = new SimpleGrantedAuthority("ROLE_" + role.getName());
            var permissionAuthorities = role.getPermissions().stream()
                .map(p -> new SimpleGrantedAuthority(p.getName()));
            return Stream.concat(Stream.of(roleAuthority), permissionAuthorities);
        }).collect(Collectors.toSet());
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
