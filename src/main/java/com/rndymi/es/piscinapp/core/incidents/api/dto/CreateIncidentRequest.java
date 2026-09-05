package com.rndymi.es.piscinapp.core.incidents.api.dto;

import com.rndymi.es.piscinapp.core.incidents.domain.Incident;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateIncidentRequest(
        @NotBlank
        @Size(
                max = Incident.DESCRIPTION_MAX_LENGTH
        )
        String description
) {

}
