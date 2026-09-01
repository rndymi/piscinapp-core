package com.rndymi.es.piscinapp.core.crews.application.exception;

import java.util.UUID;

public class CrewMemberNotFoundException
        extends RuntimeException {

    public CrewMemberNotFoundException(
            UUID crewId,
            UUID employeeId
    ) {

        super(
                "Employee "
                        + employeeId
                        + " is not a member of crew "
                        + crewId
        );
    }
}
