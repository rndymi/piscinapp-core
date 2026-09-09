package com.rndymi.es.piscinapp.core.platform.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserResolver {

    private static final String ADMIN_AUTHORITY =
            "ROLE_ADMIN";

    public AuthenticatedUser resolve(
            Authentication authentication
    ) {

        if (
                authentication == null
                        || !authentication.isAuthenticated()
                        || authentication.getName() == null
                        || authentication.getName().isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Authenticated principal is required"
            );
        }

        boolean admin =
                authentication
                        .getAuthorities()
                        .stream()
                        .anyMatch(
                                authority ->
                                        ADMIN_AUTHORITY.equals(
                                                authority.getAuthority()
                                        )
                        );

        return new AuthenticatedUser(
                authentication.getName(),
                admin
        );
    }
}
