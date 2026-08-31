package com.rndymi.es.piscinapp.core.identity.application.exception;

public class UsernameConflictException
        extends RuntimeException {

    public UsernameConflictException() {

        super(
                "Username already exists"
        );
    }
}
