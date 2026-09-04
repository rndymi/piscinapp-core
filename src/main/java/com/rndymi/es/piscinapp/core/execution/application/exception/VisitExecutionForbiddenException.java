package com.rndymi.es.piscinapp.core.execution.application.exception;

public class VisitExecutionForbiddenException
        extends RuntimeException {

    public VisitExecutionForbiddenException() {

        super(
                "Authenticated account is not authorized to execute the visit"
        );
    }
}
