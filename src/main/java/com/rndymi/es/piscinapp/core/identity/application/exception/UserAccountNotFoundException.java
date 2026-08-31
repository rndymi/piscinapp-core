package com.rndymi.es.piscinapp.core.identity.application.exception;

import java.util.UUID;

public class UserAccountNotFoundException
        extends RuntimeException {

    public UserAccountNotFoundException(
            UUID id
    ) {

        super(
                "User account not found: "
                        + id
        );
    }

    public UserAccountNotFoundException(
            String username
    ) {

        super(
                "User account not found: "
                        + username
        );
    }
}
