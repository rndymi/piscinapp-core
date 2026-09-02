package com.rndymi.es.piscinapp.core.identity.application;

import com.rndymi.es.piscinapp.core.identity.application.exception.InvalidCurrentPasswordException;
import com.rndymi.es.piscinapp.core.identity.application.exception.LastAdminConflictException;
import com.rndymi.es.piscinapp.core.identity.application.exception.UserAccountNotFoundException;
import com.rndymi.es.piscinapp.core.identity.application.exception.UsernameConflictException;
import com.rndymi.es.piscinapp.core.identity.domain.SecurityRole;
import com.rndymi.es.piscinapp.core.identity.domain.UserAccount;
import com.rndymi.es.piscinapp.core.identity.persistence.UserAccountRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class UserAccountService {

    private static final int
            PASSWORD_MIN_LENGTH = 12;

    private static final int
            PASSWORD_MAX_LENGTH = 128;

    private static final String
            USERNAME_CONSTRAINT =
            "uk_user_accounts_username";

    private final UserAccountRepository
            userAccountRepository;

    private final PasswordEncoder
            passwordEncoder;

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

        Set<SecurityRole> normalizedRoles =
                normalizeRoles(
                        roles
                );

        if (
                userAccountRepository
                        .existsByUsername(
                                normalizedUsername
                        )
        ) {

            throw new UsernameConflictException();
        }

        UserAccount account =
                new UserAccount(
                        UUID.randomUUID(),
                        normalizedUsername,
                        passwordEncoder.encode(
                                rawPassword
                        ),
                        enabled,
                        normalizedRoles
                );

        try {

            return userAccountRepository
                    .saveAndFlush(
                            account
                    );
        } catch (
                DataIntegrityViolationException
                        exception
        ) {

            if (
                    isUsernameConstraintViolation(
                            exception
                    )
            ) {

                throw new UsernameConflictException();
            }

            throw exception;
        }
    }

    @Transactional
    public UserAccount createOwnerAccount(
            String username,
            String rawPassword
    ) {

        if (
                userAccountRepository
                        .existsByOwnerTrue()
        ) {

            throw new IllegalStateException(
                    "Protected Owner already exists"
            );
        }

        String normalizedUsername =
                validateAndNormalizeUsername(
                        username
                );

        validatePassword(
                rawPassword
        );

        if (
                userAccountRepository
                        .existsByUsername(
                                normalizedUsername
                        )
        ) {

            throw new UsernameConflictException();
        }

        UserAccount account =
                UserAccount.createOwner(
                        UUID.randomUUID(),
                        normalizedUsername,
                        passwordEncoder.encode(
                                rawPassword
                        )
                );

        try {

            return userAccountRepository
                    .saveAndFlush(
                            account
                    );

        } catch (
                DataIntegrityViolationException
                        exception
        ) {

            if (
                    isUsernameConstraintViolation(
                            exception
                    )
            ) {

                throw new UsernameConflictException();
            }

            throw exception;
        }
    }

    @Transactional(
            readOnly = true
    )
    public UserAccount getAccount(
            UUID id
    ) {

        return userAccountRepository
                .findWithRolesById(
                        id
                )
                .orElseThrow(
                        () ->
                                new UserAccountNotFoundException(
                                        id
                                )
                );
    }

    @Transactional(
            readOnly = true
    )
    public List<UserAccount> listAccounts() {

        return userAccountRepository
                .findAllByOrderByUsernameAsc();
    }

    @Transactional(
            readOnly = true
    )
    public UserAccount getCurrentAccount(
            String principalName
    ) {

        return findByPrincipalName(
                principalName
        );
    }

    @Transactional
    public UserAccount replaceRoles(
            UUID id,
            Set<SecurityRole> roles
    ) {

        UserAccount account =
                getAccountForUpdate(
                        id
                );

        Set<SecurityRole> normalizedRoles =
                normalizeRoles(
                        roles
                );

        if (
                wouldRemoveLastEnabledAdmin(
                        account,
                        normalizedRoles.contains(
                                SecurityRole.ADMIN
                        ),
                        account.isEnabled()
                )
        ) {

            throw new LastAdminConflictException();
        }

        account.replaceRoles(
                normalizedRoles
        );

        return account;
    }

    @Transactional
    public UserAccount updateStatus(
            UUID id,
            boolean enabled
    ) {

        UserAccount account =
                getAccountForUpdate(
                        id
                );

        if (
                wouldRemoveLastEnabledAdmin(
                        account,
                        account.getRoles()
                                .contains(
                                        SecurityRole.ADMIN
                                ),
                        enabled
                )
        ) {

            throw new LastAdminConflictException();
        }

        if (enabled) {

            account.enable();
        } else {

            account.disable();
        }

        return account;
    }

    @Transactional
    public void changeOwnPassword(
            String principalName,
            String currentPassword,
            String newPassword
    ) {

        UserAccount account =
                findByPrincipalName(
                        principalName
                );

        if (
                currentPassword == null
                        ||
                        !passwordEncoder.matches(
                                currentPassword,
                                account.getPasswordHash()
                        )
        ) {

            throw new InvalidCurrentPasswordException();
        }

        validatePassword(
                newPassword
        );

        account.changePasswordHash(
                passwordEncoder.encode(
                        newPassword
                )
        );
    }

    @Transactional
    public void setPasswordAsAdmin(
            UUID id,
            String newPassword
    ) {

        UserAccount account =
                getAccountForUpdate(
                        id
                );

        validatePassword(
                newPassword
        );

        account.changePasswordHash(
                passwordEncoder.encode(
                        newPassword
                )
        );
    }

    private UserAccount getAccountForUpdate(
            UUID id
    ) {

        return userAccountRepository
                .findWithRolesById(
                        id
                )
                .orElseThrow(
                        () ->
                                new UserAccountNotFoundException(
                                        id
                                )
                );
    }

    private UserAccount findByPrincipalName(
            String principalName
    ) {

        String username =
                validateAndNormalizeUsername(
                        principalName
                );

        return userAccountRepository
                .findByUsername(
                        username
                )
                .orElseThrow(
                        () ->
                                new UserAccountNotFoundException(
                                        username
                                )
                );
    }

    private boolean
    wouldRemoveLastEnabledAdmin(
            UserAccount target,
            boolean adminAfterChange,
            boolean enabledAfterChange
    ) {

        boolean currentlyEnabledAdmin =
                target.isEnabled()
                        &&
                        target.getRoles()
                                .contains(
                                        SecurityRole.ADMIN
                                );

        boolean remainsEnabledAdmin =
                enabledAfterChange
                        &&
                        adminAfterChange;

        if (
                !currentlyEnabledAdmin
                        ||
                        remainsEnabledAdmin
        ) {

            return false;
        }

        List<UserAccount> enabledAdmins =
                userAccountRepository
                        .findEnabledAccountsByRoleForUpdate(
                                SecurityRole.ADMIN
                        );

        return enabledAdmins.size() <= 1
                &&
                enabledAdmins.stream()
                        .anyMatch(
                                account ->
                                        account.getId()
                                                .equals(
                                                        target.getId()
                                                )
                        );
    }

    private String
    validateAndNormalizeUsername(
            String username
    ) {

        String normalizedUsername =
                UsernameNormalizer.normalize(
                        username
                );

        if (
                normalizedUsername == null
                        ||
                        normalizedUsername.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Username must not be blank"
            );
        }

        if (
                normalizedUsername.length()
                        >
                        UserAccount
                                .USERNAME_MAX_LENGTH
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
                        ||
                        rawPassword.isBlank()
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

    private Set<SecurityRole> normalizeRoles(
            Set<SecurityRole> roles
    ) {

        if (
                roles == null
                        ||
                        roles.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "At least one security role is required"
            );
        }

        EnumSet<SecurityRole> normalized =
                EnumSet.copyOf(
                        roles
                );

        if (
                normalized.contains(
                        SecurityRole.ADMIN
                )
        ) {

            normalized.add(
                    SecurityRole.USER
            );
        }

        if (
                !normalized.contains(
                        SecurityRole.USER
                )
        ) {

            throw new IllegalArgumentException(
                    "USER role is required"
            );
        }

        return normalized;
    }

    private boolean
    isUsernameConstraintViolation(
            DataIntegrityViolationException exception
    ) {

        Throwable current =
                exception;

        while (
                current != null
        ) {

            String message =
                    current.getMessage();

            if (
                    message != null
                            &&
                            message
                                    .toLowerCase(
                                            Locale.ROOT
                                    )
                                    .contains(
                                            USERNAME_CONSTRAINT
                                    )
            ) {

                return true;
            }

            current =
                    current.getCause();
        }

        return false;
    }
}
