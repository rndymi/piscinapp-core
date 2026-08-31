package com.rndymi.es.piscinapp.core.identity.application.exception;

public class LastAdminConflictException
        extends RuntimeException {

    public LastAdminConflictException() {

        super(
                "At least one enabled administrator must remain"
        );
    }
}
