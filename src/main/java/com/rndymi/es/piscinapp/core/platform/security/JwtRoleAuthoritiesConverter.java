package com.rndymi.es.piscinapp.core.platform.security;

import com.rndymi.es.piscinapp.core.identity.domain.SecurityRole;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JwtRoleAuthoritiesConverter
        implements Converter<
        Jwt,
        Collection<GrantedAuthority>
        > {

    private final JwtGrantedAuthoritiesConverter
            scopeAuthoritiesConverter =
            new JwtGrantedAuthoritiesConverter();

    @Override
    public Collection<GrantedAuthority> convert(
            Jwt jwt
    ) {

        List<GrantedAuthority> authorities =
                new ArrayList<>();

        Collection<GrantedAuthority>
                scopeAuthorities =
                scopeAuthoritiesConverter.convert(
                        jwt
                );

        if (scopeAuthorities != null) {

            authorities.addAll(
                    scopeAuthorities
            );
        }

        Object rolesClaim =
                jwt.getClaim("roles");

        if (!(rolesClaim instanceof Collection<?> roles)) {

            return authorities;
        }

        roles.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .filter(
                        JwtRoleAuthoritiesConverter
                                ::isPiscinAppRole
                )
                .map(
                        role ->
                                new SimpleGrantedAuthority(
                                        "ROLE_" + role
                                )
                )
                .forEach(
                        authorities::add
                );

        return authorities;
    }

    private static boolean isPiscinAppRole(
            String role
    ) {

        try {

            SecurityRole.valueOf(role);

            return true;

        } catch (IllegalArgumentException exception) {

            return false;
        }
    }
}
