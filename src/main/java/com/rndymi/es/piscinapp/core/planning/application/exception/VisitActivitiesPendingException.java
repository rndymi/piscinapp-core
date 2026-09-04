package com.rndymi.es.piscinapp.core.planning.application.exception;

import java.util.UUID;

public class VisitActivitiesPendingException
        extends RuntimeException {

    private final UUID visitId;

    public VisitActivitiesPendingException(
            UUID visitId
    ) {

        super(
                "Visit has pending maintenance activities"
        );

        this.visitId =
                visitId;
    }

    public UUID getVisitId() {
        return visitId;
    }
}
