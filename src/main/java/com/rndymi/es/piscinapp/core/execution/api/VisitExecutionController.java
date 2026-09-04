package com.rndymi.es.piscinapp.core.execution.api;

import com.rndymi.es.piscinapp.core.execution.api.dto.CreateVisitObservationRequest;
import com.rndymi.es.piscinapp.core.execution.api.dto.VisitActivityExecutionResponse;
import com.rndymi.es.piscinapp.core.execution.api.dto.VisitExecutionResponse;
import com.rndymi.es.piscinapp.core.execution.api.dto.VisitObservationResponse;
import com.rndymi.es.piscinapp.core.execution.application.VisitExecutionService;
import com.rndymi.es.piscinapp.core.execution.domain.VisitObservation;
import com.rndymi.es.piscinapp.core.planning.application.VisitActivityExecutionReference;
import com.rndymi.es.piscinapp.core.planning.application.VisitExecutionReference;
import com.rndymi.es.piscinapp.core.planning.domain.VisitStatus;
import com.rndymi.es.piscinapp.core.platform.web.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(
        "/api/v1/visits"
)
public class VisitExecutionController {

    private final VisitExecutionService visitExecutionService;
    private final AssignedVisitPageRequestFactory pageRequestFactory;

    @GetMapping(
            "/assigned"
    )
    @Operation(
            summary =
                    "Return visits assigned to the authenticated operational employee"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Assigned visits returned"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Operational employee association required"
            )
    })
    public PageResponse<VisitExecutionResponse>
    getAssignedVisits(
            Authentication authentication,
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
            VisitStatus status
    ) {

        Page<VisitExecutionReference> visits =
                visitExecutionService
                        .findAssignedVisits(
                                authentication.getName(),
                                date,
                                fromDate,
                                toDate,
                                status,
                                pageRequestFactory
                                        .create(
                                                page,
                                                size,
                                                sort
                                        )
                        );

        return PageResponse.from(
                visits,
                VisitExecutionResponse::from
        );
    }

    @GetMapping(
            "/{visitId}/execution"
    )
    @Operation(
            summary =
                    "Return execution state for one visit"
    )
    public VisitExecutionResponse getExecutionDetail(
            @PathVariable
            UUID visitId,
            Authentication authentication
    ) {

        VisitExecutionReference visit =
                isAdmin(
                        authentication
                )
                        ?
                        visitExecutionService
                                .getExecutionDetailForAdmin(
                                        visitId
                                )
                        :
                        visitExecutionService
                                .getExecutionDetailForAssignedActor(
                                        visitId,
                                        authentication.getName()
                                );

        return VisitExecutionResponse.from(
                visit
        );
    }

    @PutMapping(
            "/{visitId}/start"
    )
    @Operation(
            summary =
                    "Start an assigned planned visit"
    )
    public VisitExecutionResponse startVisit(
            @PathVariable
            UUID visitId,
            Authentication authentication
    ) {

        return VisitExecutionResponse.from(
                visitExecutionService
                        .startVisit(
                                visitId,
                                authentication.getName()
                        )
        );
    }

    @PutMapping(
            "/{visitId}/activities/{activityId}/complete"
    )
    @Operation(
            summary =
                    "Complete one selected maintenance activity"
    )
    public VisitActivityExecutionResponse completeActivity(
            @PathVariable
            UUID visitId,
            @PathVariable
            UUID activityId,
            Authentication authentication
    ) {

        VisitActivityExecutionReference activity =
                visitExecutionService
                        .completeActivity(
                                visitId,
                                activityId,
                                authentication.getName()
                        );

        return VisitActivityExecutionResponse.from(
                activity
        );
    }

    @PostMapping(
            "/{visitId}/observations"
    )
    @Operation(
            summary =
                    "Record an operational visit observation"
    )
    public ResponseEntity<VisitObservationResponse>
    createObservation(
            @PathVariable
            UUID visitId,
            Authentication authentication,
            @Valid
            @RequestBody
            CreateVisitObservationRequest request
    ) {

        VisitObservation observation =
                visitExecutionService
                        .addObservation(
                                visitId,
                                request.text(),
                                authentication.getName()
                        );

        VisitObservationResponse response =
                VisitObservationResponse.from(
                        observation
                );

        return ResponseEntity
                .created(
                        URI.create(
                                "/api/v1/visits/"
                                        + visitId
                                        + "/observations/"
                                        + response.id()
                        )
                )
                .body(
                        response
                );
    }

    @GetMapping(
            "/{visitId}/observations"
    )
    @Operation(
            summary =
                    "Return operational observations for one visit"
    )
    public List<VisitObservationResponse>
    getObservations(
            @PathVariable
            UUID visitId,
            Authentication authentication
    ) {

        List<VisitObservation> observations =
                isAdmin(
                        authentication
                )
                        ?
                        visitExecutionService
                                .getObservationsForAdmin(
                                        visitId
                                )
                        :
                        visitExecutionService
                                .getObservationsForAssignedActor(
                                        visitId,
                                        authentication.getName()
                                );

        return observations
                .stream()
                .map(
                        VisitObservationResponse::from
                )
                .toList();
    }

    @PutMapping(
            "/{visitId}/complete"
    )
    @Operation(
            summary =
                    "Complete an assigned visit"
    )
    public VisitExecutionResponse completeVisit(
            @PathVariable
            UUID visitId,
            Authentication authentication
    ) {

        return VisitExecutionResponse.from(
                visitExecutionService
                        .completeVisit(
                                visitId,
                                authentication.getName()
                        )
        );
    }

    private boolean isAdmin(
            Authentication authentication
    ) {

        return authentication
                .getAuthorities()
                .stream()
                .anyMatch(
                        authority ->
                                "ROLE_ADMIN"
                                        .equals(
                                                authority
                                                        .getAuthority()
                                        )
                );
    }
}
