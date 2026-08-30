package com.rndymi.es.piscinapp.core.identity.application;

import com.rndymi.es.piscinapp.core.identity.domain.SecurityRole;
import com.rndymi.es.piscinapp.core.identity.domain.UserAccount;
import com.rndymi.es.piscinapp.core.identity.persistence.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.EnumSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceTests {

    @Mock
    private UserAccountRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserAccountService service;

    @BeforeEach
    void setUp() {

        service = new UserAccountService(
                repository,
                passwordEncoder
        );
    }

    @Test
    void shouldNormalizeUsernameAndEncodePassword() {

        String rawPassword =
                "test-password-123";

        when(
                repository.existsByUsername(
                        "admin.core"
                )
        )
                .thenReturn(false);

        when(
                passwordEncoder.encode(
                        rawPassword
                )
        )
                .thenReturn(
                        "{bcrypt}encoded-password"
                );

        when(
                repository.save(
                        any(UserAccount.class)
                )
        )
                .thenAnswer(
                        invocation ->
                                invocation.getArgument(0)
                );

        service.createAccount(
                "  Admin.Core  ",
                rawPassword,
                true,
                EnumSet.of(
                        SecurityRole.USER,
                        SecurityRole.ADMIN
                )
        );

        ArgumentCaptor<UserAccount> captor =
                ArgumentCaptor.forClass(
                        UserAccount.class
                );

        verify(repository).save(
                captor.capture()
        );

        UserAccount account =
                captor.getValue();

        assertThat(
                account.getId()
        )
                .isNotNull();

        assertThat(
                account.getUsername()
        )
                .isEqualTo(
                        "admin.core"
                );

        assertThat(
                account.getPasswordHash()
        )
                .isEqualTo(
                        "{bcrypt}encoded-password"
                );

        assertThat(
                account.getPasswordHash()
        )
                .isNotEqualTo(
                        rawPassword
                );

        assertThat(
                account.isEnabled()
        )
                .isTrue();

        assertThat(
                account.getRoles()
        )
                .containsExactlyInAnyOrder(
                        SecurityRole.USER,
                        SecurityRole.ADMIN
                );
    }

    @Test
    void shouldRejectBlankUsername() {

        assertThatThrownBy(
                () ->
                        service.createAccount(
                                "   ",
                                "test-password-123",
                                true,
                                EnumSet.of(
                                        SecurityRole.USER
                                )
                        )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                );

        verify(
                repository,
                never()
        )
                .save(
                        any()
                );
    }

    @Test
    void shouldRejectShortPassword() {

        assertThatThrownBy(
                () ->
                        service.createAccount(
                                "user.test",
                                "short",
                                true,
                                EnumSet.of(
                                        SecurityRole.USER
                                )
                        )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                );

        verify(
                repository,
                never()
        )
                .save(
                        any()
                );
    }

    @Test
    void shouldRejectUsernameLongerThanMaximum() {

        String username =
                "a".repeat(
                        UserAccount.USERNAME_MAX_LENGTH
                                + 1
                );

        assertThatThrownBy(
                () ->
                        service.createAccount(
                                username,
                                "test-password-123",
                                true,
                                EnumSet.of(
                                        SecurityRole.USER
                                )
                        )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                );
    }

    @Test
    void shouldRejectDuplicateNormalizedUsername() {

        when(
                repository.existsByUsername(
                        "existing.user"
                )
        )
                .thenReturn(true);

        assertThatThrownBy(
                () ->
                        service.createAccount(
                                " Existing.User ",
                                "test-password-123",
                                true,
                                EnumSet.of(
                                        SecurityRole.USER
                                )
                        )
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessage(
                        "Username already exists"
                );

        verify(
                passwordEncoder,
                never()
        )
                .encode(
                        any()
                );
    }
}
