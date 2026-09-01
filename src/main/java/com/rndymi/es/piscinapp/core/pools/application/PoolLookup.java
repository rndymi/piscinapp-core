package com.rndymi.es.piscinapp.core.pools.application;

import java.util.UUID;

public interface PoolLookup {

    PoolReference requirePool(
            UUID poolId
    );
}
