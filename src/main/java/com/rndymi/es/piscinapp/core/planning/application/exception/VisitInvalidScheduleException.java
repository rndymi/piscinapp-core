package com.rndymi.es.piscinapp.core.planning.application.exception;

import java.time.LocalDate;
import java.time.LocalTime;

public class VisitInvalidScheduleException
        extends RuntimeException {

    public VisitInvalidScheduleException(
            LocalDate plannedDate,
            LocalTime plannedTime
    ) {

        super(
                "Visit schedule must not be in the past: "
                        + plannedDate
                        + "T"
                        + plannedTime
        );
    }
}
