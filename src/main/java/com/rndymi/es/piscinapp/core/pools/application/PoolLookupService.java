package com.rndymi.es.piscinapp.core.pools.application;

import com.rndymi.es.piscinapp.core.pools.application.exception.PoolNotFoundException;
import com.rndymi.es.piscinapp.core.pools.domain.SwimmingPool;
import com.rndymi.es.piscinapp.core.pools.persistence.SwimmingPoolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PoolLookupService
        implements PoolLookup {

    private final SwimmingPoolRepository
            swimmingPoolRepository;

    public PoolLookupService(
            SwimmingPoolRepository swimmingPoolRepository
    ) {

        this.swimmingPoolRepository =
                swimmingPoolRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PoolReference requirePool(
            UUID poolId
    ) {

        SwimmingPool pool =
                swimmingPoolRepository
                        .findById(
                                poolId
                        )
                        .orElseThrow(
                                () ->
                                        new PoolNotFoundException(
                                                poolId
                                        )
                        );

        return new PoolReference(
                pool.getId(),
                pool.isActive()
        );
    }
}
