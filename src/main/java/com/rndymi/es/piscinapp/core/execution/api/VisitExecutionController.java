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
import com.rndymi.es.piscinapp.core.platform.security.AuthenticatedUser;
import com.rndymi.es.piscinapp.core.platform.security.AuthenticatedUserResolver;
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
    private final AuthenticatedUserResolver authenticatedUserResolver;

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

        AuthenticatedUser user =
                authenticatedUserResolver.resolve(
                        authentication
                );

        Page<VisitExecutionReference> visits =
                visitExecutionService
                        .findAssignedVisits(
                                user.username(),
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

        AuthenticatedUser user =
                authenticatedUserResolver.resolve(
                        authentication
                );

        VisitExecutionReference visit =
                user.admin()
                        ?
                        visitExecutionService
                                .getExecutionDetailForAdmin(
                                        visitId
                                )
                        :
                        visitExecutionService
                                .getExecutionDetailForAssignedActor(
                                        visitId,
                                        user.username()
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

        AuthenticatedUser user =
                authenticatedUserResolver.resolve(
                        authentication
                );

        return VisitExecutionResponse.from(
                visitExecutionService
                        .startVisit(
                                visitId,
                                user.username()
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

        AuthenticatedUser user =
                authenticatedUserResolver.resolve(
                        authentication
                );

        VisitActivityExecutionReference activity =
                visitExecutionService
                        .completeActivity(
                                visitId,
                                activityId,
                                user.username()
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

        AuthenticatedUser user =
                authenticatedUserResolver.resolve(
                        authentication
                );

        VisitObservation observation =
                visitExecutionService
                        .addObservation(
                                visitId,
                                request.text(),
                                user.username()
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

        AuthenticatedUser user =
                authenticatedUserResolver.resolve(
                        authentication
                );

        List<VisitObservation> observations =
                user.admin()
                        ?
                        visitExecutionService
                                .getObservationsForAdmin(
                                        visitId
                                )
                        :
                        visitExecutionService
                                .getObservationsForAssignedActor(
                                        visitId,
                                        user.username()
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

        AuthenticatedUser user =
                authenticatedUserResolver.resolve(
                        authentication
                );

        return VisitExecutionResponse.from(
                visitExecutionService
                        .completeVisit(
                                visitId,
                                user.username()
                        )
        );
    }
}
