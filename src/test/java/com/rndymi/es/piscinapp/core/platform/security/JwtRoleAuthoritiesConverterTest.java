package com.rndymi.es.piscinapp.core.platform.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtRoleAuthoritiesConverterTest {

    private final JwtRoleAuthoritiesConverter
            converter =
            new JwtRoleAuthoritiesConverter();

    @Test
    void shouldConvertKnownPiscinAppRoles() {

        Jwt jwt =
                jwtWithRoles(
                        List.of(
                                "USER",
                                "ADMIN"
                        )
                );

        Collection<GrantedAuthority> authorities =
                converter.convert(jwt);

        assertThat(
                authorities
                        .stream()
                        .map(
                                GrantedAuthority::getAuthority
                        )
        )
                .contains(
                        "ROLE_USER",
                        "ROLE_ADMIN"
                );
    }

    @Test
    void shouldIgnoreUnknownRole() {

        Jwt jwt =
                jwtWithRoles(
                        List.of(
                                "USER",
                                "SUPER_ADMIN"
                        )
                );

        Collection<GrantedAuthority> authorities =
                converter.convert(jwt);

        assertThat(
                authorities
                        .stream()
                        .map(
                                GrantedAuthority::getAuthority
                        )
        )
                .contains(
                        "ROLE_USER"
                )
                .doesNotContain(
                        "ROLE_SUPER_ADMIN"
                );
    }

    private Jwt jwtWithRoles(
            List<String> roles
    ) {

        Instant issuedAt =
                Instant.now();

        return Jwt
                .withTokenValue(
                        "test-token"
                )
                .header(
                        "alg",
                        "RS256"
                )
                .issuer(
                        "http://localhost:8080"
                )
                .issuedAt(
                        issuedAt
                )
                .expiresAt(
                        issuedAt.plusSeconds(
                                900
                        )
                )
                .subject(
                        "test-user"
                )
                .claim(
                        "roles",
                        roles
                )
                .build();
    }
}
