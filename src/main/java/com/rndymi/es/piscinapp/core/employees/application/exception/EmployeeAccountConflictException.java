package com.rndymi.es.piscinapp.core.employees.application.exception;

import java.util.UUID;

public class EmployeeAccountConflictException
        extends RuntimeException {

    public EmployeeAccountConflictException(
            UUID accountId
    ) {

        super(
                "User account is already associated with another employee: "
                        + accountId
        );
    }
}
