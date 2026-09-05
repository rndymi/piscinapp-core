package com.rndymi.es.piscinapp.core.incidents.application.exception;

public class IncidentResolutionForbiddenException
        extends RuntimeException {

    public IncidentResolutionForbiddenException() {

        super(
                "Authenticated account is not authorized to resolve the incident"
        );
    }
}
