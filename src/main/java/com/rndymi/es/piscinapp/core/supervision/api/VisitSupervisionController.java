package com.rndymi.es.piscinapp.core.supervision.api;

import com.rndymi.es.piscinapp.core.supervision.api.dto.VisitSupervisionResponse;
import com.rndymi.es.piscinapp.core.supervision.application.SupervisionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(
        "/api/v1/visits"
)
public class VisitSupervisionController {

    private final SupervisionService supervisionService;

    @GetMapping(
            "/{visitId}/supervision"
    )
    @Operation(
            summary =
                    "Return the operational supervision projection for one visit"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Visit supervision returned"
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
                    description = "ADMIN or current assigned crew supervisor required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Visit or assigned crew not found"
            )
    })
    public VisitSupervisionResponse getVisitSupervision(
            @PathVariable
            UUID visitId,
            Authentication authentication
    ) {

        return VisitSupervisionResponse.from(
                supervisionService
                        .getVisitSupervision(
                                visitId,
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
