package com.rndymi.es.piscinapp.core.identity.application.exception;

public class InvalidCurrentPasswordException
        extends RuntimeException {

    public InvalidCurrentPasswordException() {

        super(
                "The current password is invalid"
        );
    }
}
