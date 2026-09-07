package com.rndymi.es.piscinapp.core.platform.security;

public record AuthenticatedUser(
        String username,
        boolean admin
) {
}
