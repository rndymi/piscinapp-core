package com.rndymi.es.piscinapp.core.identity.application;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UsernameNormalizerTests {

    @Test
    void shouldNormalizeUsername() {

        assertThat(
                UsernameNormalizer.normalize(
                        "  Admin.Core  "
                )
        )
                .isEqualTo(
                        "admin.core"
                );
    }

    @Test
    void shouldPreserveNullUsername() {

        assertThat(
                UsernameNormalizer.normalize(
                        null
                )
        )
                .isNull();
    }
}
