package com.rndymi.es.piscinapp.core.platform.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtKeyConfigurationTest {

    private final JwtKeyConfiguration configuration =
            new JwtKeyConfiguration();

    @Test
    void shouldGenerateDevelopmentRsaSigningKey() throws Exception {
        JWKSource<SecurityContext> jwkSource =
                configuration.developmentJwkSource();

        JWKMatcher matcher =
                new JWKMatcher.Builder()
                        .keyType(KeyType.RSA)
                        .build();

        JWKSelector selector =
                new JWKSelector(matcher);

        List<JWK> keys =
                jwkSource.get(
                        selector,
                        null
                );

        assertThat(keys)
                .hasSize(1);

        JWK key = keys.getFirst();

        assertThat(key.getKeyType())
                .isEqualTo(KeyType.RSA);

        assertThat(key.getKeyID())
                .isEqualTo("piscinapp-core-dev");

        assertThat(key.isPrivate())
                .isTrue();
    }

    @Test
    void shouldRejectMissingProductionSigningMaterial() {
        JwtKeyProperties properties =
                new JwtKeyProperties();

        assertThatThrownBy(
                () ->
                        configuration.productionJwkSource(
                                properties
                        )
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessageContaining(
                        "JWT_KEYSTORE_BASE64"
                );
    }
}
