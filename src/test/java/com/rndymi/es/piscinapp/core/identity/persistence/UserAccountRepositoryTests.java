package com.rndymi.es.piscinapp.core.identity.persistence;

import com.rndymi.es.piscinapp.core.identity.domain.SecurityRole;
import com.rndymi.es.piscinapp.core.identity.domain.UserAccount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class UserAccountRepositoryTests {

    @Autowired
    private UserAccountRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldPersistAccountAndRoles() {

        UUID id =
                UUID.randomUUID();

        UserAccount account =
                new UserAccount(
                        id,
                        "persistence.user",
                        "{noop}encoded-password",
                        false,
                        EnumSet.of(
                                SecurityRole.USER,
                                SecurityRole.ADMIN
                        )
                );

        repository.saveAndFlush(
                account
        );

        UserAccount persisted =
                repository.findById(
                                id
                        )
                        .orElseThrow();

        assertThat(
                persisted.getId()
        )
                .isEqualTo(id);

        assertThat(
                persisted.getUsername()
        )
                .isEqualTo(
                        "persistence.user"
                );

        assertThat(
                persisted.getPasswordHash()
        )
                .isEqualTo(
                        "{noop}encoded-password"
                );

        assertThat(
                persisted.isEnabled()
        )
                .isFalse();

        assertThat(
                persisted.getRoles()
        )
                .containsExactlyInAnyOrder(
                        SecurityRole.USER,
                        SecurityRole.ADMIN
                );

        assertThat(
                repository.existsByRole(
                        SecurityRole.ADMIN
                )
        )
                .isTrue();
    }

    @Test
    void shouldPersistRolesByEnumName() {

        UUID id =
                UUID.randomUUID();

        repository.saveAndFlush(
                new UserAccount(
                        id,
                        "role.persistence",
                        "{noop}encoded-password",
                        true,
                        EnumSet.of(
                                SecurityRole.USER,
                                SecurityRole.ADMIN
                        )
                )
        );

        List<String> roles =
                jdbcTemplate.queryForList(
                        """
                        select role
                        from account_roles
                        where account_id = ?
                        """,
                        String.class,
                        id
                );

        assertThat(
                roles
        )
                .containsExactlyInAnyOrder(
                        "USER",
                        "ADMIN"
                );
    }

    @Test
    void shouldRejectDuplicateUsernameAtDatabaseLevel() {

        repository.saveAndFlush(
                new UserAccount(
                        UUID.randomUUID(),
                        "duplicate.user",
                        "{noop}first-password",
                        true,
                        EnumSet.of(
                                SecurityRole.USER
                        )
                )
        );

        assertThatThrownBy(
                () ->
                        repository.saveAndFlush(
                                new UserAccount(
                                        UUID.randomUUID(),
                                        "duplicate.user",
                                        "{noop}second-password",
                                        true,
                                        EnumSet.of(
                                                SecurityRole.USER
                                        )
                                )
                        )
        )
                .isInstanceOf(
                        DataIntegrityViolationException.class
                );
    }
}
