package com.rndymi.es.piscinapp.core.execution.application;

import java.time.Instant;
import java.util.UUID;

public record VisitObservationReference(
        UUID id,
        String text,
        Instant createdAt,
        UUID createdByAccountId,
        UUID createdByEmployeeId
) {
}
