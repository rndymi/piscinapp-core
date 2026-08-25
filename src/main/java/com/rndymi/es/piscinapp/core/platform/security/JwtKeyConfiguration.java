package com.rndymi.es.piscinapp.core.platform.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.ByteArrayInputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

@Configuration
@EnableConfigurationProperties(JwtKeyProperties.class)
public class JwtKeyConfiguration {

    @Bean
    @Profile({"dev", "test"})
    JWKSource<SecurityContext> developmentJwkSource() {
        RSAKey rsaKey = generateRsaKey();
        JWKSet jwkSet = new JWKSet(rsaKey);

        return (selector, context) ->
                selector.select(jwkSet);
    }

    @Bean
    @Profile("prod")
    JWKSource<SecurityContext> productionJwkSource(
            JwtKeyProperties properties
    ) {
        RSAKey rsaKey = loadProductionRsaKey(properties);
        JWKSet jwkSet = new JWKSet(rsaKey);

        return (selector, context) ->
                selector.select(jwkSet);
    }

    private RSAKey generateRsaKey() {
        try {
            KeyPairGenerator generator =
                    KeyPairGenerator.getInstance("RSA");

            generator.initialize(2048);

            KeyPair keyPair =
                    generator.generateKeyPair();

            RSAPublicKey publicKey =
                    (RSAPublicKey) keyPair.getPublic();

            RSAPrivateKey privateKey =
                    (RSAPrivateKey) keyPair.getPrivate();

            return new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .keyID("piscinapp-core-dev")
                    .build();

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to generate development JWT signing key",
                    exception
            );
        }
    }

    private RSAKey loadProductionRsaKey(
            JwtKeyProperties properties
    ) {
        requireProductionProperty(
                properties.getKeyStoreBase64(),
                "JWT_KEYSTORE_BASE64"
        );

        requireProductionProperty(
                properties.getKeyStorePassword(),
                "JWT_KEYSTORE_PASSWORD"
        );

        requireProductionProperty(
                properties.getKeyAlias(),
                "JWT_KEY_ALIAS"
        );

        requireProductionProperty(
                properties.getKeyId(),
                "JWT_KEY_ID"
        );

        try {
            byte[] keyStoreBytes =
                    Base64.getDecoder().decode(
                            properties.getKeyStoreBase64()
                    );

            KeyStore keyStore =
                    KeyStore.getInstance("PKCS12");

            keyStore.load(
                    new ByteArrayInputStream(keyStoreBytes),
                    properties
                            .getKeyStorePassword()
                            .toCharArray()
            );

            String keyPassword =
                    properties.getKeyPassword() == null
                            || properties.getKeyPassword().isBlank()
                            ? properties.getKeyStorePassword()
                            : properties.getKeyPassword();

            Key key =
                    keyStore.getKey(
                            properties.getKeyAlias(),
                            keyPassword.toCharArray()
                    );

            if (!(key instanceof RSAPrivateKey privateKey)) {
                throw new IllegalStateException(
                        "Configured signing key is not RSA"
                );
            }

            Certificate certificate =
                    keyStore.getCertificate(
                            properties.getKeyAlias()
                    );

            if (certificate == null
                    || !(certificate.getPublicKey()
                    instanceof RSAPublicKey publicKey)) {
                throw new IllegalStateException(
                        "Configured RSA certificate is missing"
                );
            }

            return new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .keyID(properties.getKeyId())
                    .build();

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to load production JWT signing key",
                    exception
            );
        }
    }

    private void requireProductionProperty(
            String value,
            String environmentVariable
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    environmentVariable
                            + " must be configured in production"
            );
        }
    }
}
