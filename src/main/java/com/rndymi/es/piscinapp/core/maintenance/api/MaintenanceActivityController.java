package com.rndymi.es.piscinapp.core.maintenance.api;

import com.rndymi.es.piscinapp.core.maintenance.api.dto.CreateMaintenanceActivityRequest;
import com.rndymi.es.piscinapp.core.maintenance.api.dto.MaintenanceActivityResponse;
import com.rndymi.es.piscinapp.core.maintenance.api.dto.UpdateMaintenanceActivityRequest;
import com.rndymi.es.piscinapp.core.maintenance.api.dto.UpdateMaintenanceActivityStatusRequest;
import com.rndymi.es.piscinapp.core.maintenance.application.MaintenanceActivityService;
import com.rndymi.es.piscinapp.core.platform.web.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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
import java.util.UUID;

@RestController
@RequestMapping(
        "/api/v1/maintenance-activities"
)
public class MaintenanceActivityController {

    private final MaintenanceActivityService
            maintenanceActivityService;

    private final MaintenanceActivityPageRequestFactory
            pageRequestFactory;

    public MaintenanceActivityController(
            MaintenanceActivityService maintenanceActivityService,
            MaintenanceActivityPageRequestFactory pageRequestFactory
    ) {

        this.maintenanceActivityService =
                maintenanceActivityService;

        this.pageRequestFactory =
                pageRequestFactory;
    }

    @PostMapping
    @Operation(
            summary =
                    "Create a maintenance activity"
    )
    public ResponseEntity<MaintenanceActivityResponse>
    createActivity(
            @Valid
            @RequestBody
            CreateMaintenanceActivityRequest request
    ) {

        MaintenanceActivityResponse response =
                MaintenanceActivityResponse.from(
                        maintenanceActivityService
                                .createActivity(
                                        request.name(),
                                        request.description()
                                )
                );

        return ResponseEntity
                .created(
                        URI.create(
                                "/api/v1/maintenance-activities/"
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
                    "Return one maintenance activity"
    )
    public MaintenanceActivityResponse getActivity(
            @PathVariable
            UUID id
    ) {

        return MaintenanceActivityResponse.from(
                maintenanceActivityService
                        .getActivity(
                                id
                        )
        );
    }

    @GetMapping
    @Operation(
            summary =
                    "Search maintenance activities"
    )
    public PageResponse<MaintenanceActivityResponse>
    listActivities(
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

        return PageResponse.from(
                maintenanceActivityService
                        .listActivities(
                                active,
                                search,
                                pageRequestFactory
                                        .create(
                                                page,
                                                size,
                                                sort
                                        )
                        ),
                MaintenanceActivityResponse::from
        );
    }

    @PutMapping(
            "/{id}"
    )
    @Operation(
            summary =
                    "Update maintenance activity fields"
    )
    public MaintenanceActivityResponse updateActivity(
            @PathVariable
            UUID id,
            @Valid
            @RequestBody
            UpdateMaintenanceActivityRequest request
    ) {

        return MaintenanceActivityResponse.from(
                maintenanceActivityService
                        .updateActivity(
                                id,
                                request.name(),
                                request.description()
                        )
        );
    }

    @PutMapping(
            "/{id}/status"
    )
    @Operation(
            summary =
                    "Activate or deactivate a maintenance activity"
    )
    public MaintenanceActivityResponse updateStatus(
            @PathVariable
            UUID id,
            @Valid
            @RequestBody
            UpdateMaintenanceActivityStatusRequest request
    ) {

        return MaintenanceActivityResponse.from(
                maintenanceActivityService
                        .updateStatus(
                                id,
                                request.active()
                        )
        );
    }
}
