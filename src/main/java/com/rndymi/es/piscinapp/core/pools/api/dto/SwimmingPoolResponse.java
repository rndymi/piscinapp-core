package com.rndymi.es.piscinapp.core.pools.api.dto;

import com.rndymi.es.piscinapp.core.pools.domain.SwimmingPool;

import java.util.UUID;

public record SwimmingPoolResponse(
        UUID id,
        String name,
        String address,
        boolean active
) {

    public static SwimmingPoolResponse from(
            SwimmingPool pool
    ) {

        return new SwimmingPoolResponse(
                pool.getId(),
                pool.getName(),
                pool.getAddress(),
                pool.isActive()
        );
    }
}
