package com.rndymi.es.piscinapp.core.execution.api.dto;

import com.rndymi.es.piscinapp.core.execution.domain.VisitObservation;

import java.time.Instant;
import java.util.UUID;

public record VisitObservationResponse(
        UUID id,
        UUID visitId,
        String text,
        Instant createdAt,
        UUID createdByAccountId,
        UUID createdByEmployeeId
) {

    public static VisitObservationResponse from(
            VisitObservation source
    ) {

        return new VisitObservationResponse(
                source.getId(),
                source.getVisitId(),
                source.getText(),
                source.getCreatedAt(),
                source.getCreatedByAccountId(),
                source.getCreatedByEmployeeId()
        );
    }
}
