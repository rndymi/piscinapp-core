package com.rndymi.es.piscinapp.core.pools.application;

import com.rndymi.es.piscinapp.core.pools.application.exception.PoolNotFoundException;
import com.rndymi.es.piscinapp.core.pools.domain.SwimmingPool;
import com.rndymi.es.piscinapp.core.pools.persistence.SwimmingPoolRepository;
import com.rndymi.es.piscinapp.core.pools.persistence.SwimmingPoolSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SwimmingPoolService {

    private final SwimmingPoolRepository swimmingPoolRepository;

    @Transactional
    public SwimmingPool createPool(
            String name,
            String address
    ) {

        SwimmingPool pool =
                new SwimmingPool(
                        UUID.randomUUID(),
                        normalizeRequired(
                                name
                        ),
                        normalizeRequired(
                                address
                        )
                );

        return swimmingPoolRepository
                .saveAndFlush(
                        pool
                );
    }

    @Transactional(readOnly = true)
    public SwimmingPool getPool(
            UUID poolId
    ) {

        return swimmingPoolRepository
                .findById(
                        poolId
                )
                .orElseThrow(
                        () ->
                                new PoolNotFoundException(
                                        poolId
                                )
                );
    }

    @Transactional(readOnly = true)
    public Page<SwimmingPool> listPools(
            Boolean active,
            String search,
            Pageable pageable
    ) {

        Specification<SwimmingPool>
                specification =
                SwimmingPoolSpecifications
                        .activeEquals(
                                active
                        )
                        .and(
                                SwimmingPoolSpecifications
                                        .nameOrAddressContains(
                                                search
                                        )
                        );

        return swimmingPoolRepository
                .findAll(
                        specification,
                        pageable
                );
    }

    @Transactional
    public SwimmingPool updatePool(
            UUID poolId,
            String name,
            String address
    ) {

        SwimmingPool pool =
                getPoolForUpdate(
                        poolId
                );

        pool.update(
                normalizeRequired(
                        name
                ),
                normalizeRequired(
                        address
                )
        );

        return pool;
    }

    @Transactional
    public SwimmingPool updateStatus(
            UUID poolId,
            boolean active
    ) {

        SwimmingPool pool =
                getPoolForUpdate(
                        poolId
                );

        if (active) {

            pool.activate();

        } else {

            pool.deactivate();
        }

        return pool;
    }

    private SwimmingPool getPoolForUpdate(
            UUID poolId
    ) {

        return swimmingPoolRepository
                .findById(
                        poolId
                )
                .orElseThrow(
                        () ->
                                new PoolNotFoundException(
                                        poolId
                                )
                );
    }

    private String normalizeRequired(
            String value
    ) {

        return value.strip();
    }
}
