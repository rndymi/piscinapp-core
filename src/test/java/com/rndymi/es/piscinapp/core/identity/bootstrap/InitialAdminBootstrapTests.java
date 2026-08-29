package com.rndymi.es.piscinapp.core.identity.bootstrap;

import com.rndymi.es.piscinapp.core.identity.application.UserAccountService;
import com.rndymi.es.piscinapp.core.identity.domain.SecurityRole;
import com.rndymi.es.piscinapp.core.identity.persistence.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

class InitialAdminBootstrapTests {

    private UserAccountRepository repository;

    private UserAccountService accountService;

    private BootstrapAdminProperties properties;

    private InitialAdminBootstrap bootstrap;

    @BeforeEach
    void setUp() {

        repository =
                mock(
                        UserAccountRepository.class
                );

        accountService =
                mock(
                        UserAccountService.class
                );

        properties =
                new BootstrapAdminProperties();

        properties.setUsername(
                "  Bootstrap.Admin  "
        );

        properties.setPassword(
                "bootstrap-password"
        );

        bootstrap =
                new InitialAdminBootstrap(
                        repository,
                        accountService,
                        properties
                );
    }

    @Test
    void shouldCreateInitialAdministratorWhenNoneExists() {

        when(
                repository.existsByRole(
                        SecurityRole.ADMIN
                )
        )
                .thenReturn(false);

        bootstrap.run(
                new DefaultApplicationArguments(
                        new String[0]
                )
        );

        verify(accountService)
                .createAccount(
                        eq(
                                "  Bootstrap.Admin  "
                        ),
                        eq(
                                "bootstrap-password"
                        ),
                        eq(true),
                        eq(
                                Set.of(
                                        SecurityRole.USER,
                                        SecurityRole.ADMIN
                                )
                        )
                );
    }

    @Test
    void shouldDoNothingWhenAdministratorAlreadyExists() {

        when(
                repository.existsByRole(
                        SecurityRole.ADMIN
                )
        )
                .thenReturn(true);

        bootstrap.run(
                new DefaultApplicationArguments(
                        new String[0]
                )
        );

        verify(
                accountService,
                never()
        )
                .createAccount(
                        any(),
                        any(),
                        eq(true),
                        any()
                );
    }

    @Test
    void shouldFailWhenBootstrapConfigurationIsInvalid() {

        properties.setUsername(
                null
        );

        properties.setPassword(
                null
        );

        when(
                repository.existsByRole(
                        SecurityRole.ADMIN
                )
        )
                .thenReturn(false);

        when(
                accountService.createAccount(
                        any(),
                        any(),
                        eq(true),
                        any()
                )
        )
                .thenThrow(
                        new IllegalArgumentException(
                                "Username must not be blank"
                        )
                );

        assertThatThrownBy(
                () ->
                        bootstrap.run(
                                new DefaultApplicationArguments(
                                        new String[0]
                                )
                        )
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessage(
                        "Valid bootstrap administrator credentials are required when no administrator exists"
                );
    }
}
