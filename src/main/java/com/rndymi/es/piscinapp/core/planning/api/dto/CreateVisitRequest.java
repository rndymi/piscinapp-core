package com.rndymi.es.piscinapp.core.planning.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record CreateVisitRequest(

        @NotNull
        UUID poolId,

        @NotNull
        UUID crewId,

        @NotNull
        LocalDate plannedDate,

        @NotNull
        @JsonFormat(pattern = "HH:mm")
        LocalTime plannedTime,

        @NotEmpty
        List<@NotNull UUID> maintenanceActivityIds,

        @Size(max = 1000)
        String notes
) {
}
