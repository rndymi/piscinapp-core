package com.rndymi.es.piscinapp.core.maintenance.application.exception;

import java.util.UUID;

public class InactiveResourceException
        extends RuntimeException {

    public InactiveResourceException(
            String resourceType,
            UUID resourceId
    ) {

        super(
                resourceType
                        + " is inactive: "
                        + resourceId
        );
    }
}
