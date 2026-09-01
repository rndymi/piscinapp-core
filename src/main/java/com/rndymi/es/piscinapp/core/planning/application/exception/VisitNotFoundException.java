package com.rndymi.es.piscinapp.core.planning.application.exception;

import java.util.UUID;

public class VisitNotFoundException
        extends RuntimeException {

    public VisitNotFoundException(
            UUID visitId
    ) {

        super(
                "Visit not found: "
                        + visitId
        );
    }
}