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

class InitialOwnerBootstrapTests {

    private UserAccountRepository repository;

    private UserAccountService accountService;

    private BootstrapOwnerProperties properties;

    private InitialOwnerBootstrap bootstrap;

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
                new BootstrapOwnerProperties();

        properties.setUsername(
                "  Bootstrap.Owner  "
        );

        properties.setPassword(
                "bootstrap-password"
        );

        bootstrap =
                new InitialOwnerBootstrap(
                        repository,
                        accountService,
                        properties
                );
    }

    @Test
    void shouldCreateProtectedOwnerWhenNoneExists() {

        when(
                repository.existsByOwnerTrue()
        )
                .thenReturn(
                        false
                );

        bootstrap.run(
                new DefaultApplicationArguments(
                        new String[0]
                )
        );

        verify(
                accountService
        )
                .createOwnerAccount(
                        "  Bootstrap.Owner  ",
                        "bootstrap-password"
                );
    }

    @Test
    void shouldDoNothingWhenOwnerAlreadyExists() {

        when(
                repository.existsByOwnerTrue()
        )
                .thenReturn(
                        true
                );

        bootstrap.run(
                new DefaultApplicationArguments(
                        new String[0]
                )
        );

        verify(
                accountService,
                never()
        )
                .createOwnerAccount(
                        any(),
                        any()
                );
    }

    @Test
    void shouldCreateOwnerEvenWhenNormalAdministratorExists() {

        when(
                repository.existsByOwnerTrue()
        )
                .thenReturn(
                        false
                );

        bootstrap.run(
                new DefaultApplicationArguments(
                        new String[0]
                )
        );

        verify(
                accountService
        )
                .createOwnerAccount(
                        "  Bootstrap.Owner  ",
                        "bootstrap-password"
                );

        verify(
                repository,
                never()
        )
                .existsByRole(
                        SecurityRole.ADMIN
                );
    }

    @Test
    void shouldFailWhenOwnerBootstrapConfigurationIsInvalid() {

        properties.setUsername(
                null
        );

        properties.setPassword(
                null
        );

        when(
                repository.existsByOwnerTrue()
        )
                .thenReturn(
                        false
                );

        when(
                accountService.createOwnerAccount(
                        any(),
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
                        "Valid bootstrap Owner credentials are required when no protected Owner exists"
                );
    }
}
