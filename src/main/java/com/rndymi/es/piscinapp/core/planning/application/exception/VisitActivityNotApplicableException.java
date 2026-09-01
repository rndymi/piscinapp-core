package com.rndymi.es.piscinapp.core.planning.application.exception;

import java.util.UUID;

public class VisitActivityNotApplicableException
        extends RuntimeException {

    public VisitActivityNotApplicableException(
            UUID poolId,
            UUID activityId
    ) {

        super(
                "Maintenance activity "
                        + activityId
                        + " is not applicable to swimming pool "
                        + poolId
        );
    }
}
