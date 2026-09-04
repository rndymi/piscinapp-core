package com.rndymi.es.piscinapp.core.incidents.application;

import com.rndymi.es.piscinapp.core.incidents.domain.IncidentStatus;

import java.util.UUID;

public record IncidentSearchCriteria(
        IncidentStatus status,
        UUID visitId,
        UUID createdByEmployeeId
) {
}
