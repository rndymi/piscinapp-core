package com.rndymi.es.piscinapp.core.identity.application;

import com.rndymi.es.piscinapp.core.identity.application.exception.UsernameConflictException;
import com.rndymi.es.piscinapp.core.identity.application.exception.LastAdminConflictException;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
                repository.saveAndFlush(
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

        verify(repository).saveAndFlush(
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
                        UsernameConflictException.class
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

    @Test
    void shouldRejectDisablingLastEnabledAdmin() {

        UUID id =
                UUID.randomUUID();

        UserAccount admin =
                new UserAccount(
                        id,
                        "last.admin",
                        "{bcrypt}password",
                        true,
                        EnumSet.of(
                                SecurityRole.USER,
                                SecurityRole.ADMIN
                        )
                );

        when(
                repository.findWithRolesById(
                        id
                )
        )
                .thenReturn(
                        Optional.of(
                                admin
                        )
                );

        when(
                repository
                        .findEnabledAccountsByRoleForUpdate(
                                SecurityRole.ADMIN
                        )
        )
                .thenReturn(
                        List.of(
                                admin
                        )
                );

        assertThatThrownBy(
                () ->
                        service.updateStatus(
                                id,
                                false
                        )
        )
                .isInstanceOf(
                        LastAdminConflictException.class
                );
    }

    @Test
    void shouldAllowDisablingAdminWhenAnotherEnabledAdminExists() {

        UUID targetId =
                UUID.randomUUID();

        UserAccount target =
                new UserAccount(
                        targetId,
                        "first.admin",
                        "{bcrypt}password",
                        true,
                        EnumSet.of(
                                SecurityRole.USER,
                                SecurityRole.ADMIN
                        )
                );

        UserAccount another =
                new UserAccount(
                        UUID.randomUUID(),
                        "second.admin",
                        "{bcrypt}password",
                        true,
                        EnumSet.of(
                                SecurityRole.USER,
                                SecurityRole.ADMIN
                        )
                );

        when(
                repository.findWithRolesById(
                        targetId
                )
        )
                .thenReturn(
                        Optional.of(
                                target
                        )
                );

        when(
                repository
                        .findEnabledAccountsByRoleForUpdate(
                                SecurityRole.ADMIN
                        )
        )
                .thenReturn(
                        List.of(
                                target,
                                another
                        )
                );

        UserAccount updated =
                service.updateStatus(
                        targetId,
                        false
                );

        assertThat(
                updated.isEnabled()
        )
                .isFalse();
    }

    @Test
    void shouldEnsureAdminAlsoContainsUserRole() {

        when(
                repository.existsByUsername(
                        "admin.only"
                )
        )
                .thenReturn(
                        false
                );

        when(
                passwordEncoder.encode(
                        "test-password-123"
                )
        )
                .thenReturn(
                        "{bcrypt}encoded"
                );

        when(
                repository.saveAndFlush(
                        any(
                                UserAccount.class
                        )
                )
        )
                .thenAnswer(
                        invocation ->
                                invocation.getArgument(
                                        0
                                )
                );

        UserAccount account =
                service.createAccount(
                        "admin.only",
                        "test-password-123",
                        true,
                        EnumSet.of(
                                SecurityRole.ADMIN
                        )
                );

        assertThat(
                account.getRoles()
        )
                .containsExactlyInAnyOrder(
                        SecurityRole.USER,
                        SecurityRole.ADMIN
                );
    }
}
