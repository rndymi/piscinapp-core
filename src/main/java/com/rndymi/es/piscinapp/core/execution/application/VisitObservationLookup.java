package com.rndymi.es.piscinapp.core.execution.application;

import java.util.List;
import java.util.UUID;

public interface VisitObservationLookup {

    List<VisitObservationReference> findObservationsByVisitId(
            UUID visitId
    );
}
