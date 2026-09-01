package com.rndymi.es.piscinapp.core.pools.application;

import java.util.UUID;

public record PoolReference(
        UUID id,
        boolean active
) {
}
