package com.rndymi.es.piscinapp.core.identity.security;

import com.rndymi.es.piscinapp.core.identity.domain.SecurityRole;
import com.rndymi.es.piscinapp.core.identity.domain.UserAccount;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class PiscinAppUserDetails
        implements UserDetails {

    private final String username;
    private final String passwordHash;
    private final boolean enabled;
    private final List<GrantedAuthority> authorities;

    public PiscinAppUserDetails(
            UserAccount account
    ) {

        this.username =
                account.getUsername();

        this.passwordHash =
                account.getPasswordHash();

        this.enabled =
                account.isEnabled();

        this.authorities =
                account.getRoles()
                        .stream()
                        .map(
                                PiscinAppUserDetails
                                        ::toAuthority
                        )
                        .map(
                                authority ->
                                        (GrantedAuthority)
                                                authority
                        )
                        .toList();
    }

    @Override
    public Collection<? extends GrantedAuthority>
    getAuthorities() {

        return authorities;
    }

    @Override
    public String getPassword() {

        return passwordHash;
    }

    @Override
    public String getUsername() {

        return username;
    }

    @Override
    public boolean isAccountNonExpired() {

        return true;
    }

    @Override
    public boolean isAccountNonLocked() {

        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {

        return true;
    }

    @Override
    public boolean isEnabled() {

        return enabled;
    }

    private static SimpleGrantedAuthority
    toAuthority(
            SecurityRole role
    ) {

        return new SimpleGrantedAuthority(
                "ROLE_" + role.name()
        );
    }
}
