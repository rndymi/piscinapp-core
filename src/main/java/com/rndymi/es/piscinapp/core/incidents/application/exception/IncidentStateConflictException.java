package com.rndymi.es.piscinapp.core.incidents.application.exception;

import java.util.UUID;

public class IncidentStateConflictException
        extends RuntimeException {

    public IncidentStateConflictException(
            UUID incidentId,
            String reason
    ) {

        super(
                "Incident "
                        + incidentId
                        + " cannot be modified: "
                        + reason
        );
    }
}
