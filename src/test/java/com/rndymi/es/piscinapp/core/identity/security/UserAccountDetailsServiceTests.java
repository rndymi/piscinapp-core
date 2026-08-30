package com.rndymi.es.piscinapp.core.identity.security;

import com.rndymi.es.piscinapp.core.identity.domain.SecurityRole;
import com.rndymi.es.piscinapp.core.identity.domain.UserAccount;
import com.rndymi.es.piscinapp.core.identity.persistence.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserAccountDetailsServiceTests {

    private UserAccountRepository repository;

    private UserAccountDetailsService service;

    @BeforeEach
    void setUp() {

        repository =
                mock(
                        UserAccountRepository.class
                );

        service =
                new UserAccountDetailsService(
                        repository
                );
    }

    @Test
    void shouldNormalizeUsernameAndExposeAuthorities() {

        UserAccount account =
                new UserAccount(
                        UUID.randomUUID(),
                        "admin.core",
                        "{bcrypt}encoded",
                        true,
                        EnumSet.of(
                                SecurityRole.USER,
                                SecurityRole.ADMIN
                        )
                );

        when(
                repository.findByUsername(
                        "admin.core"
                )
        )
                .thenReturn(
                        Optional.of(account)
                );

        UserDetails userDetails =
                service.loadUserByUsername(
                        "  Admin.Core  "
                );

        verify(repository)
                .findByUsername(
                        "admin.core"
                );

        assertThat(
                userDetails.getUsername()
        )
                .isEqualTo(
                        "admin.core"
                );

        assertThat(
                userDetails.getPassword()
        )
                .isEqualTo(
                        "{bcrypt}encoded"
                );

        assertThat(
                userDetails.isEnabled()
        )
                .isTrue();

        assertThat(
                userDetails.getAuthorities()
        )
                .extracting(
                        authority ->
                                authority.getAuthority()
                )
                .containsExactlyInAnyOrder(
                        "ROLE_USER",
                        "ROLE_ADMIN"
                );
    }

    @Test
    void shouldExposeDisabledAccountAsDisabled() {

        UserAccount account =
                new UserAccount(
                        UUID.randomUUID(),
                        "disabled.user",
                        "{bcrypt}encoded",
                        false,
                        EnumSet.of(
                                SecurityRole.USER
                        )
                );

        when(
                repository.findByUsername(
                        "disabled.user"
                )
        )
                .thenReturn(
                        Optional.of(account)
                );

        UserDetails userDetails =
                service.loadUserByUsername(
                        "disabled.user"
                );

        assertThat(
                userDetails.isEnabled()
        )
                .isFalse();
    }

    @Test
    void shouldRejectUnknownAccount() {

        when(
                repository.findByUsername(
                        "missing.user"
                )
        )
                .thenReturn(
                        Optional.empty()
                );

        assertThatThrownBy(
                () ->
                        service.loadUserByUsername(
                                "Missing.User"
                        )
        )
                .isInstanceOf(
                        UsernameNotFoundException.class
                );
    }
}
