package com.rndymi.es.piscinapp.core.identity.application;

import com.rndymi.es.piscinapp.core.identity.domain.SecurityRole;
import com.rndymi.es.piscinapp.core.identity.domain.UserAccount;
import com.rndymi.es.piscinapp.core.identity.persistence.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
public class UserAccountService {

    private static final int PASSWORD_MIN_LENGTH = 12;
    private static final int PASSWORD_MAX_LENGTH = 128;

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder
    ) {

        this.userAccountRepository =
                userAccountRepository;

        this.passwordEncoder =
                passwordEncoder;
    }

    @Transactional
    public UserAccount createAccount(
            String username,
            String rawPassword,
            boolean enabled,
            Set<SecurityRole> roles
    ) {

        String normalizedUsername =
                validateAndNormalizeUsername(
                        username
                );

        validatePassword(
                rawPassword
        );

        validateRoles(
                roles
        );

        if (
                userAccountRepository
                        .existsByUsername(
                                normalizedUsername
                        )
        ) {

            throw new IllegalStateException(
                    "Username already exists"
            );
        }

        UserAccount account =
                new UserAccount(
                        UUID.randomUUID(),
                        normalizedUsername,
                        passwordEncoder.encode(
                                rawPassword
                        ),
                        enabled,
                        roles
                );

        return userAccountRepository.save(
                account
        );
    }

    private String validateAndNormalizeUsername(
            String username
    ) {

        String normalizedUsername =
                UsernameNormalizer.normalize(
                        username
                );

        if (
                normalizedUsername == null
                        || normalizedUsername.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Username must not be blank"
            );
        }

        if (
                normalizedUsername.length()
                        > UserAccount.USERNAME_MAX_LENGTH
        ) {

            throw new IllegalArgumentException(
                    "Username must not exceed 100 characters"
            );
        }

        return normalizedUsername;
    }

    private void validatePassword(
            String rawPassword
    ) {

        if (
                rawPassword == null
                        || rawPassword.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Password must not be blank"
            );
        }

        if (
                rawPassword.length()
                        < PASSWORD_MIN_LENGTH
                        ||
                        rawPassword.length()
                                > PASSWORD_MAX_LENGTH
        ) {

            throw new IllegalArgumentException(
                    "Password must contain between 12 and 128 characters"
            );
        }
    }

    private void validateRoles(
            Set<SecurityRole> roles
    ) {

        if (
                roles == null
                        || roles.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "At least one security role is required"
            );
        }
    }
}
