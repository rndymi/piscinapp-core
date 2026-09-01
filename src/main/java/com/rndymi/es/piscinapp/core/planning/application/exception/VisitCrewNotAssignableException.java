package com.rndymi.es.piscinapp.core.planning.application.exception;

import java.util.UUID;

public class VisitCrewNotAssignableException
        extends RuntimeException {

    public VisitCrewNotAssignableException(
            UUID crewId
    ) {

        super(
                "Crew is not assignable for visit planning: "
                        + crewId
        );
    }
}
