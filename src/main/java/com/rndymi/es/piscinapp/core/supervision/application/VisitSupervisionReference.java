package com.rndymi.es.piscinapp.core.supervision.application;

import com.rndymi.es.piscinapp.core.execution.application.VisitObservationReference;
import com.rndymi.es.piscinapp.core.incidents.application.IncidentReference;
import com.rndymi.es.piscinapp.core.planning.application.VisitExecutionReference;

import java.util.List;

public record VisitSupervisionReference(
        VisitExecutionReference visit,
        List<VisitObservationReference> observations,
        List<IncidentReference> incidents
) {
}
