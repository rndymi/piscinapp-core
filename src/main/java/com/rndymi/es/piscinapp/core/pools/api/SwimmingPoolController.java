package com.rndymi.es.piscinapp.core.pools.api;

import com.rndymi.es.piscinapp.core.platform.web.PageResponse;
import com.rndymi.es.piscinapp.core.pools.api.dto.CreateSwimmingPoolRequest;
import com.rndymi.es.piscinapp.core.pools.api.dto.SwimmingPoolResponse;
import com.rndymi.es.piscinapp.core.pools.api.dto.UpdateSwimmingPoolRequest;
import com.rndymi.es.piscinapp.core.pools.api.dto.UpdateSwimmingPoolStatusRequest;
import com.rndymi.es.piscinapp.core.pools.application.SwimmingPoolService;
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
        "/api/v1/pools"
)
public class SwimmingPoolController {

    private final SwimmingPoolService
            swimmingPoolService;

    private final SwimmingPoolPageRequestFactory
            pageRequestFactory;

    public SwimmingPoolController(
            SwimmingPoolService swimmingPoolService,
            SwimmingPoolPageRequestFactory pageRequestFactory
    ) {

        this.swimmingPoolService =
                swimmingPoolService;

        this.pageRequestFactory =
                pageRequestFactory;
    }

    @PostMapping
    @Operation(
            summary =
                    "Create a swimming pool"
    )
    public ResponseEntity<SwimmingPoolResponse>
    createPool(
            @Valid
            @RequestBody
            CreateSwimmingPoolRequest request
    ) {

        SwimmingPoolResponse response =
                SwimmingPoolResponse.from(
                        swimmingPoolService
                                .createPool(
                                        request.name(),
                                        request.address()
                                )
                );

        return ResponseEntity
                .created(
                        URI.create(
                                "/api/v1/pools/"
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
                    "Return one swimming pool"
    )
    public SwimmingPoolResponse getPool(
            @PathVariable
            UUID id
    ) {

        return SwimmingPoolResponse.from(
                swimmingPoolService
                        .getPool(
                                id
                        )
        );
    }

    @GetMapping
    @Operation(
            summary =
                    "Search swimming pools"
    )
    public PageResponse<SwimmingPoolResponse>
    listPools(
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
                swimmingPoolService
                        .listPools(
                                active,
                                search,
                                pageRequestFactory
                                        .create(
                                                page,
                                                size,
                                                sort
                                        )
                        ),
                SwimmingPoolResponse::from
        );
    }

    @PutMapping(
            "/{id}"
    )
    @Operation(
            summary =
                    "Update swimming pool fields"
    )
    public SwimmingPoolResponse updatePool(
            @PathVariable
            UUID id,
            @Valid
            @RequestBody
            UpdateSwimmingPoolRequest request
    ) {

        return SwimmingPoolResponse.from(
                swimmingPoolService
                        .updatePool(
                                id,
                                request.name(),
                                request.address()
                        )
        );
    }

    @PutMapping(
            "/{id}/status"
    )
    @Operation(
            summary =
                    "Activate or deactivate a swimming pool"
    )
    public SwimmingPoolResponse updateStatus(
            @PathVariable
            UUID id,
            @Valid
            @RequestBody
            UpdateSwimmingPoolStatusRequest request
    ) {

        return SwimmingPoolResponse.from(
                swimmingPoolService
                        .updateStatus(
                                id,
                                request.active()
                        )
        );
    }
}
