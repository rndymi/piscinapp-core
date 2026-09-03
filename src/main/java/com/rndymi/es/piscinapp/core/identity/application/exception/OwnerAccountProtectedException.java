package com.rndymi.es.piscinapp.core.identity.application.exception;

public class OwnerAccountProtectedException
        extends RuntimeException {

    public OwnerAccountProtectedException() {

        super(
                "Protected Owner account cannot be modified through account administration"
        );
    }
}
