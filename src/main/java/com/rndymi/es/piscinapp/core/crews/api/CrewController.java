package com.rndymi.es.piscinapp.core.crews.api;

import com.rndymi.es.piscinapp.core.crews.api.dto.CreateCrewRequest;
import com.rndymi.es.piscinapp.core.crews.api.dto.CrewResponse;
import com.rndymi.es.piscinapp.core.crews.api.dto.UpdateCrewRequest;
import com.rndymi.es.piscinapp.core.crews.api.dto.UpdateCrewStatusRequest;
import com.rndymi.es.piscinapp.core.crews.application.CrewService;
import com.rndymi.es.piscinapp.core.crews.domain.Crew;
import com.rndymi.es.piscinapp.core.platform.web.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(
        "/api/v1/crews"
)
public class CrewController {

    private final CrewService
            crewService;

    private final CrewPageRequestFactory
            pageRequestFactory;

    public CrewController(
            CrewService crewService,
            CrewPageRequestFactory pageRequestFactory
    ) {

        this.crewService =
                crewService;

        this.pageRequestFactory =
                pageRequestFactory;
    }

    @PostMapping
    @Operation(
            summary =
                    "Create a crew"
    )
    public ResponseEntity<CrewResponse>
    createCrew(
            @Valid
            @RequestBody
            CreateCrewRequest request
    ) {

        Crew crew =
                crewService
                        .createCrew(
                                request.name()
                        );

        CrewResponse response =
                toResponse(
                        crew
                );

        return ResponseEntity
                .created(
                        URI.create(
                                "/api/v1/crews/"
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
                    "Return one crew"
    )
    public CrewResponse getCrew(
            @PathVariable
            UUID id
    ) {

        return toResponse(
                crewService
                        .getCrew(
                                id
                        )
        );
    }

    @GetMapping
    @Operation(
            summary =
                    "Search crews"
    )
    public PageResponse<CrewResponse>
    listCrews(
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
            Boolean active,
            @RequestParam(
                    required = false
            )
            String search
    ) {

        Page<Crew> crews =
                crewService
                        .listCrews(
                                active,
                                search,
                                pageRequestFactory
                                        .create(
                                                page,
                                                size,
                                                sort
                                        )
                        );

        Map<UUID, List<UUID>>
                memberIdsByCrew =
                crewService
                        .getMemberIdsByCrewIds(
                                crews
                                        .getContent()
                                        .stream()
                                        .map(
                                                Crew::getId
                                        )
                                        .toList()
                        );

        return PageResponse.from(
                crews,
                crew ->
                        CrewResponse.from(
                                crew,
                                memberIdsByCrew
                                        .getOrDefault(
                                                crew.getId(),
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
                    "Update crew fields"
    )
    public CrewResponse updateCrew(
            @PathVariable
            UUID id,
            @Valid
            @RequestBody
            UpdateCrewRequest request
    ) {

        return toResponse(
                crewService
                        .updateCrew(
                                id,
                                request.name()
                        )
        );
    }

    @PutMapping(
            "/{id}/status"
    )
    @Operation(
            summary =
                    "Activate or deactivate a crew"
    )
    public CrewResponse updateStatus(
            @PathVariable
            UUID id,
            @Valid
            @RequestBody
            UpdateCrewStatusRequest request
    ) {

        return toResponse(
                crewService
                        .updateStatus(
                                id,
                                request.active()
                        )
        );
    }

    @PutMapping(
            "/{id}/members/{employeeId}"
    )
    @Operation(
            summary =
                    "Add an employee to a crew"
    )
    public ResponseEntity<Void> addMember(
            @PathVariable
            UUID id,
            @PathVariable
            UUID employeeId
    ) {

        crewService
                .addMember(
                        id,
                        employeeId
                );

        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping(
            "/{id}/members/{employeeId}"
    )
    @Operation(
            summary =
                    "Remove an employee from a crew"
    )
    public ResponseEntity<Void> removeMember(
            @PathVariable
            UUID id,
            @PathVariable
            UUID employeeId
    ) {

        crewService
                .removeMember(
                        id,
                        employeeId
                );

        return ResponseEntity
                .noContent()
                .build();
    }

    @PutMapping(
            "/{id}/supervisor/{employeeId}"
    )
    @Operation(
            summary =
                    "Assign a crew supervisor"
    )
    public ResponseEntity<Void> assignSupervisor(
            @PathVariable
            UUID id,
            @PathVariable
            UUID employeeId
    ) {

        crewService
                .assignSupervisor(
                        id,
                        employeeId
                );

        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping(
            "/{id}/supervisor"
    )
    @Operation(
            summary =
                    "Clear the crew supervisor"
    )
    public ResponseEntity<Void> clearSupervisor(
            @PathVariable
            UUID id
    ) {

        crewService
                .clearSupervisor(
                        id
                );

        return ResponseEntity
                .noContent()
                .build();
    }

    private CrewResponse toResponse(
            Crew crew
    ) {

        return CrewResponse.from(
                crew,
                crewService
                        .getMemberIds(
                                crew.getId()
                        )
        );
    }
}
