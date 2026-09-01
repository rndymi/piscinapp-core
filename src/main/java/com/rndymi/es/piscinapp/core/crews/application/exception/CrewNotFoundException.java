package com.rndymi.es.piscinapp.core.crews.application.exception;

import java.util.UUID;

public class CrewNotFoundException
        extends RuntimeException {

    public CrewNotFoundException(
            UUID crewId
    ) {

        super(
                "Crew not found: "
                        + crewId
        );
    }
}
