package com.rndymi.es.piscinapp.core.supervision.application.exception;

public class VisitSupervisionForbiddenException
        extends RuntimeException {

    public VisitSupervisionForbiddenException() {

        super(
                "Authenticated account is not authorized to supervise the visit"
        );
    }
}
