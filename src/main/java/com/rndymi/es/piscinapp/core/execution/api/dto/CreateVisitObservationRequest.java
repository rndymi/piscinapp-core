package com.rndymi.es.piscinapp.core.execution.api.dto;

import com.rndymi.es.piscinapp.core.execution.domain.VisitObservation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateVisitObservationRequest(

        @NotBlank
        @Size(
                max = VisitObservation.TEXT_MAX_LENGTH
        )
        String text
) {
}
