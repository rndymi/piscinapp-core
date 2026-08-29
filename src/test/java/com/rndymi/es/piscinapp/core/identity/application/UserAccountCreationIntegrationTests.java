package com.rndymi.es.piscinapp.core.identity.application;

import com.rndymi.es.piscinapp.core.identity.domain.SecurityRole;
import com.rndymi.es.piscinapp.core.identity.domain.UserAccount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class UserAccountCreationIntegrationTests {

    @Autowired
    private UserAccountService service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldPersistEncodedPassword() {

        String rawPassword =
                "integration-password";

        UserAccount account =
                service.createAccount(
                        "  Integration.User  ",
                        rawPassword,
                        true,
                        EnumSet.of(
                                SecurityRole.USER
                        )
                );

        assertThat(
                account.getUsername()
        )
                .isEqualTo(
                        "integration.user"
                );

        assertThat(
                account.getPasswordHash()
        )
                .isNotEqualTo(
                        rawPassword
                );

        assertThat(
                passwordEncoder.matches(
                        rawPassword,
                        account.getPasswordHash()
                )
        )
                .isTrue();
    }
}
