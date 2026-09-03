package com.rndymi.es.piscinapp.core.planning.api;

import com.rndymi.es.piscinapp.core.planning.api.dto.CreateVisitRequest;
import com.rndymi.es.piscinapp.core.planning.api.dto.UpdateVisitRequest;
import com.rndymi.es.piscinapp.core.planning.api.dto.VisitResponse;
import com.rndymi.es.piscinapp.core.planning.application.VisitSearchCriteria;
import com.rndymi.es.piscinapp.core.planning.application.VisitService;
import com.rndymi.es.piscinapp.core.planning.domain.Visit;
import com.rndymi.es.piscinapp.core.planning.domain.VisitStatus;
import com.rndymi.es.piscinapp.core.platform.web.PageResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(
        "/api/v1/visits"
)
public class VisitController {

    private final VisitService visitService;
    private final VisitPageRequestFactory pageRequestFactory;

    @PostMapping
    @Operation(
            summary =
                    "Create a planned visit"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Planned visit created"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request or malformed input"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "ADMIN role required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Referenced pool, crew, employee or maintenance activity not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Referenced resource inactive, crew not assignable, activity not applicable or invalid schedule"
            )
    })
    public ResponseEntity<VisitResponse>
    createVisit(
            @Valid
            @RequestBody
            CreateVisitRequest request
    ) {

        Visit visit =
                visitService
                        .createVisit(
                                request.poolId(),
                                request.crewId(),
                                request.plannedDate(),
                                request.plannedTime(),
                                request.maintenanceActivityIds(),
                                request.notes()
                        );

        VisitResponse response =
                toResponse(
                        visit
                );

        return ResponseEntity
                .created(
                        URI.create(
                                "/api/v1/visits/"
                                        + response.id()
                        )
                )
                .body(
                        response
                );
    }

    @GetMapping(
            "/{id}"
    )
    @Operation(
            summary =
                    "Return one planned visit"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Planned visit returned"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Malformed visit identifier"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "ADMIN role required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Visit not found"
            )
    })
    public VisitResponse getVisit(
            @PathVariable
            UUID id
    ) {

        return toResponse(
                visitService
                        .getVisit(
                                id
                        )
        );
    }

    @GetMapping
    @Operation(
            summary =
                    "Search scheduled visits"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Scheduled visits returned"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid filters, pagination or sorting"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "ADMIN role required"
            )
    })
    public PageResponse<VisitResponse>
    listVisits(
            @RequestParam(
                    defaultValue = "0"
            )
            int page,
            @RequestParam(
                    defaultValue = "20"
            )
            int size,
            @RequestParam(
                    required = false
            )
            String sort,
            @RequestParam(
                    required = false
            )
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate date,
            @RequestParam(
                    required = false
            )
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fromDate,
            @RequestParam(
                    required = false
            )
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate toDate,
            @RequestParam(
                    required = false
            )
            VisitStatus status,
            @RequestParam(
                    required = false
            )
            UUID poolId,
            @RequestParam(
                    required = false
            )
            UUID crewId
    ) {

        Page<Visit> visits =
                visitService
                        .listVisits(
                                new VisitSearchCriteria(
                                        date,
                                        fromDate,
                                        toDate,
                                        status,
                                        poolId,
                                        crewId
                                ),
                                pageRequestFactory
                                        .create(
                                                page,
                                                size,
                                                sort
                                        )
                        );

        Map<UUID, List<UUID>>
                activityIdsByVisit =
                visitService
                        .getActivityIdsByVisitIds(
                                visits
                                        .getContent()
                                        .stream()
                                        .map(
                                                Visit::getId
                                        )
                                        .toList()
                        );

        return PageResponse.from(
                visits,
                visit ->
                        VisitResponse.from(
                                visit,
                                activityIdsByVisit
                                        .getOrDefault(
                                                visit.getId(),
                                                List.of()
                                        )
                        )
        );
    }

    @PutMapping(
            "/{id}"
    )
    @Operation(
            summary =
                    "Update a planned visit"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Planned visit updated"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request or malformed input"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "ADMIN role required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Visit or referenced resource not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Visit state conflict or planning rules not satisfied"
            )
    })
    public VisitResponse updateVisit(
            @PathVariable
            UUID id,
            @Valid
            @RequestBody
            UpdateVisitRequest request
    ) {

        return toResponse(
                visitService
                        .updateVisit(
                                id,
                                request.poolId(),
                                request.crewId(),
                                request.plannedDate(),
                                request.plannedTime(),
                                request.maintenanceActivityIds(),
                                request.notes()
                        )
        );
    }

    @PutMapping(
            "/{id}/cancel"
    )
    @Operation(
            summary =
                    "Cancel a planned visit"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Planned visit cancelled"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Malformed visit identifier"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "ADMIN role required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Visit not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Visit cannot be cancelled from its current state"
            )
    })
    public VisitResponse cancelVisit(
            @PathVariable
            UUID id
    ) {

        return toResponse(
                visitService
                        .cancelVisit(
                                id
                        )
        );
    }

    private VisitResponse toResponse(
            Visit visit
    ) {

        return VisitResponse.from(
                visit,
                visitService
                        .getActivityIds(
                                visit.getId()
                        )
        );
    }
}
