package com.rndymi.es.piscinapp.core.maintenance.api;

import com.rndymi.es.piscinapp.core.maintenance.api.dto.MaintenanceActivityResponse;
import com.rndymi.es.piscinapp.core.maintenance.application.PoolMaintenanceConfigurationService;
import com.rndymi.es.piscinapp.core.platform.web.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(
        "/api/v1/pools/{poolId}/maintenance-activities"
)
public class PoolMaintenanceConfigurationController {

    private final PoolMaintenanceConfigurationService
            configurationService;

    private final MaintenanceActivityPageRequestFactory
            pageRequestFactory;

    public PoolMaintenanceConfigurationController(
            PoolMaintenanceConfigurationService configurationService,
            MaintenanceActivityPageRequestFactory pageRequestFactory
    ) {

        this.configurationService =
                configurationService;

        this.pageRequestFactory =
                pageRequestFactory;
    }

    @GetMapping
    @Operation(
            summary =
                    "Return maintenance activities configured for a swimming pool"
    )
    public PageResponse<MaintenanceActivityResponse>
    listConfiguredActivities(
            @PathVariable
            UUID poolId,
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
                configurationService
                        .listConfiguredActivities(
                                poolId,
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
            "/{activityId}"
    )
    @Operation(
            summary =
                    "Configure a maintenance activity for a swimming pool"
    )
    public ResponseEntity<Void> configure(
            @PathVariable
            UUID poolId,
            @PathVariable
            UUID activityId
    ) {

        configurationService
                .configure(
                        poolId,
                        activityId
                );

        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping(
            "/{activityId}"
    )
    @Operation(
            summary =
                    "Remove a maintenance activity configuration from a swimming pool"
    )
    public ResponseEntity<Void> remove(
            @PathVariable
            UUID poolId,
            @PathVariable
            UUID activityId
    ) {

        configurationService
                .remove(
                        poolId,
                        activityId
                );

        return ResponseEntity
                .noContent()
                .build();
    }
}
