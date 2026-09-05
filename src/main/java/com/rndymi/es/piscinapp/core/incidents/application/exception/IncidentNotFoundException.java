package com.rndymi.es.piscinapp.core.incidents.application.exception;

import java.util.UUID;

public class IncidentNotFoundException
        extends RuntimeException {

    public IncidentNotFoundException(
            UUID incidentId
    ) {

        super(
                "Incident "
                        + incidentId
                        + " was not found"
        );
    }
}
