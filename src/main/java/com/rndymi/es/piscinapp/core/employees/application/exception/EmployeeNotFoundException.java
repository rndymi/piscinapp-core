package com.rndymi.es.piscinapp.core.employees.application.exception;

import java.util.UUID;

public class EmployeeNotFoundException
        extends RuntimeException {

    public EmployeeNotFoundException(
            UUID employeeId
    ) {

        super(
                "Employee not found: "
                        + employeeId
        );
    }
}
