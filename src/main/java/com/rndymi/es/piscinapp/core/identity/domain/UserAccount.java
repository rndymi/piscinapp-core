package com.rndymi.es.piscinapp.core.identity.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(
        name = "user_accounts",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_accounts_username",
                columnNames = "username"
        )
)
public class UserAccount {

    public static final int USERNAME_MAX_LENGTH = 100;

    private static final int PASSWORD_HASH_MAX_LENGTH = 255;

    @Id
    @Column(
            nullable = false,
            updatable = false
    )
    private UUID id;

    @Column(
            nullable = false,
            length = USERNAME_MAX_LENGTH
    )
    private String username;

    @Column(
            name = "password_hash",
            nullable = false,
            length = PASSWORD_HASH_MAX_LENGTH
    )
    private String passwordHash;

    @Column(nullable = false)
    private boolean enabled;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "account_roles",
            joinColumns = @JoinColumn(
                    name = "account_id"
            )
    )
    @Column(
            name = "role",
            nullable = false,
            length = 32
    )
    @Enumerated(EnumType.STRING)
    private Set<SecurityRole> roles =
            EnumSet.noneOf(SecurityRole.class);

    protected UserAccount() {
    }

    public UserAccount(
            UUID id,
            String username,
            String passwordHash,
            boolean enabled,
            Set<SecurityRole> roles
    ) {

        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.enabled = enabled;

        this.roles = roles.isEmpty()
                ? EnumSet.noneOf(SecurityRole.class)
                : EnumSet.copyOf(roles);
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Set<SecurityRole> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public void replaceRoles(
            Set<SecurityRole> roles
    ) {

        this.roles =
                roles.isEmpty()
                        ? EnumSet.noneOf(
                        SecurityRole.class
                )
                        : EnumSet.copyOf(
                        roles
                );
    }

    public void enable() {

        this.enabled = true;
    }

    public void disable() {

        this.enabled = false;
    }

    public void changePasswordHash(
            String passwordHash
    ) {

        this.passwordHash =
                passwordHash;
    }
}
