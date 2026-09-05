package com.rndymi.es.piscinapp.core.incidents.api;

import com.rndymi.es.piscinapp.core.incidents.api.dto.CreateIncidentRequest;
import com.rndymi.es.piscinapp.core.incidents.api.dto.IncidentResponse;
import com.rndymi.es.piscinapp.core.incidents.application.IncidentSearchCriteria;
import com.rndymi.es.piscinapp.core.incidents.application.IncidentService;
import com.rndymi.es.piscinapp.core.incidents.domain.Incident;
import com.rndymi.es.piscinapp.core.incidents.domain.IncidentStatus;
import com.rndymi.es.piscinapp.core.platform.web.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(
        "/api/v1"
)
public class IncidentController {

    private final IncidentService incidentService;
    private final IncidentPageRequestFactory pageRequestFactory;

    @PostMapping(
            "/visits/{visitId}/incidents"
    )
    @Operation(
            summary =
                    "Create an incident for an assigned visit"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Incident created"
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
                    description = "Assigned active crew membership required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Visit not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Visit must be IN_PROGRESS"
            )
    })
    public ResponseEntity<IncidentResponse>
    createIncident(
            @PathVariable
            UUID visitId,
            @Valid
            @RequestBody
            CreateIncidentRequest request,
            Authentication authentication
    ) {

        Incident incident =
                incidentService
                        .createIncident(
                                visitId,
                                request.description(),
                                authentication.getName()
                        );

        IncidentResponse response =
                IncidentResponse
                        .from(
                                incident
                        );

        return ResponseEntity
                .created(
                        URI.create(
                                "/api/v1/incidents/"
                                        + response.id()
                        )
                )
                .body(
                        response
                );
    }

    @GetMapping(
            "/visits/{visitId}/incidents"
    )
    @Operation(
            summary =
                    "Return incidents for an assigned visit"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Visit incidents returned"
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
                    description = "Assigned active crew membership required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Visit not found"
            )
    })
    public List<IncidentResponse>
    getVisitIncidents(
            @PathVariable
            UUID visitId,
            Authentication authentication
    ) {

        return incidentService
                .getVisitIncidentsForActor(
                        visitId,
                        authentication.getName(),
                        isAdmin(
                                authentication
                        )
                )
                .stream()
                .map(
                        IncidentResponse::from
                )
                .toList();
    }

    @GetMapping(
            "/incidents/{incidentId}"
    )
    @Operation(
            summary =
                    "Return one incident"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Incident returned"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Malformed incident identifier"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "ADMIN or assigned active crew membership required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Incident or visit not found"
            )
    })
    public IncidentResponse getIncident(
            @PathVariable
            UUID incidentId,
            Authentication authentication
    ) {

        return IncidentResponse
                .from(
                        incidentService
                                .getIncidentForActor(
                                        incidentId,
                                        authentication.getName(),
                                        isAdmin(
                                                authentication
                                        )
                                )
                );
    }

    @GetMapping(
            "/incidents"
    )
    @Operation(
            summary =
                    "Search incidents for administrative supervision"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Incidents returned"
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
    public PageResponse<IncidentResponse>
    searchIncidents(
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
            IncidentStatus status,
            @RequestParam(
                    required = false
            )
            UUID visitId,
            @RequestParam(
                    required = false
            )
            UUID createdByEmployeeId
    ) {

        Page<Incident> incidents =
                incidentService
                        .searchIncidents(
                                new IncidentSearchCriteria(
                                        status,
                                        visitId,
                                        createdByEmployeeId
                                ),
                                pageRequestFactory
                                        .create(
                                                page,
                                                size,
                                                sort
                                        )
                        );

        return PageResponse
                .from(
                        incidents,
                        IncidentResponse::from
                );
    }

    @PutMapping(
            "/incidents/{incidentId}/resolve"
    )
    @Operation(
            summary =
                    "Resolve an incident"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Incident resolved"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Malformed incident identifier"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "ADMIN or current assigned crew supervisor required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Incident or visit not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Incident already resolved or visit state does not allow resolution"
            )
    })
    public IncidentResponse resolveIncident(
            @PathVariable
            UUID incidentId,
            Authentication authentication
    ) {

        return IncidentResponse
                .from(
                        incidentService
                                .resolveIncident(
                                        incidentId,
                                        authentication.getName(),
                                        isAdmin(
                                                authentication
                                        )
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
