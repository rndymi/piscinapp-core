package com.rndymi.es.piscinapp.core.crews.application.exception;

import java.util.UUID;

public class CrewMembershipConflictException
        extends RuntimeException {

    public CrewMembershipConflictException(
            UUID crewId,
            UUID employeeId
    ) {

        super(
                "Employee "
                        + employeeId
                        + " already belongs to crew "
                        + crewId
        );
    }
}
