package com.rndymi.es.piscinapp.core.platform.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthenticatedUserResolverTest {

    private final AuthenticatedUserResolver resolver =
            new AuthenticatedUserResolver();

    @Test
    void shouldResolveAuthenticatedUser() {

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "user.test",
                        null,
                        List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_USER"
                                )
                        )
                );

        AuthenticatedUser user =
                resolver.resolve(
                        authentication
                );

        assertThat(
                user.username()
        )
                .isEqualTo(
                        "user.test"
                );

        assertThat(
                user.admin()
        )
                .isFalse();
    }

    @Test
    void shouldResolveAdmin() {

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "admin.test",
                        null,
                        List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_USER"
                                ),
                                new SimpleGrantedAuthority(
                                        "ROLE_ADMIN"
                                )
                        )
                );

        AuthenticatedUser user =
                resolver.resolve(
                        authentication
                );

        assertThat(
                user.username()
        )
                .isEqualTo(
                        "admin.test"
                );

        assertThat(
                user.admin()
        )
                .isTrue();
    }

    @Test
    void shouldRejectMissingAuthentication() {

        assertThatThrownBy(
                () ->
                        resolver.resolve(
                                null
                        )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessageContaining(
                        "Authenticated principal"
                );
    }
}
