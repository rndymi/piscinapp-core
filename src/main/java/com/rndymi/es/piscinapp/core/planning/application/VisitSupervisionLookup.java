package com.rndymi.es.piscinapp.core.planning.application;

import java.util.UUID;

public interface VisitSupervisionLookup {

    VisitExecutionReference requireVisitForSupervision(
            UUID visitId
    );
}
