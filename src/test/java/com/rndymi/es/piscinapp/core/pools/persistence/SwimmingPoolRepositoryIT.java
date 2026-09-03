package com.rndymi.es.piscinapp.core.pools.persistence;

import com.rndymi.es.piscinapp.core.pools.domain.SwimmingPool;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class SwimmingPoolRepositoryIT {

    @Autowired
    private SwimmingPoolRepository
            swimmingPoolRepository;

    @Test
    void shouldPersistSwimmingPool() {

        SwimmingPool pool =
                new SwimmingPool(
                        UUID.randomUUID(),
                        "Residencial Norte",
                        "Calle Example 10, Madrid"
                );

        SwimmingPool saved =
                swimmingPoolRepository
                        .saveAndFlush(
                                pool
                        );

        assertThat(
                swimmingPoolRepository
                        .findById(
                                saved.getId()
                        )
        )
                .isPresent()
                .get()
                .satisfies(
                        persisted -> {

                            assertThat(
                                    persisted.getName()
                            )
                                    .isEqualTo(
                                            "Residencial Norte"
                                    );

                            assertThat(
                                    persisted.getAddress()
                            )
                                    .isEqualTo(
                                            "Calle Example 10, Madrid"
                                    );

                            assertThat(
                                    persisted.isActive()
                            )
                                    .isTrue();
                        }
                );
    }

    @Test
    void shouldPreserveInactiveSwimmingPool() {

        SwimmingPool pool =
                new SwimmingPool(
                        UUID.randomUUID(),
                        "Residencial Norte",
                        "Calle Example 10, Madrid"
                );

        pool.deactivate();

        SwimmingPool saved =
                swimmingPoolRepository
                        .saveAndFlush(
                                pool
                        );

        assertThat(
                swimmingPoolRepository
                        .findById(
                                saved.getId()
                        )
        )
                .isPresent()
                .get()
                .extracting(
                        SwimmingPool::isActive
                )
                .isEqualTo(
                        false
                );
    }
}
