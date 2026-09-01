package com.rndymi.es.piscinapp.core.crews.application.exception;

public class CrewSupervisorConflictException
        extends RuntimeException {

    public CrewSupervisorConflictException(
            String message
    ) {

        super(
                message
        );
    }
}
