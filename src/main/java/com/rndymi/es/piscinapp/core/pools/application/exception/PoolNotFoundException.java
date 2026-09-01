package com.rndymi.es.piscinapp.core.pools.application.exception;

import java.util.UUID;

public class PoolNotFoundException
        extends RuntimeException {

    public PoolNotFoundException(
            UUID poolId
    ) {

        super(
                "Swimming pool not found: "
                        + poolId
        );
    }
}
