package com.rndymi.es.piscinapp.core.planning.application.exception;

import com.rndymi.es.piscinapp.core.planning.domain.VisitStatus;

import java.util.UUID;

public class VisitStateConflictException
        extends RuntimeException {

    public VisitStateConflictException(
            UUID visitId,
            VisitStatus status
    ) {

        super(
                "Visit "
                        + visitId
                        + " cannot be modified while status is "
                        + status
        );
    }
}
