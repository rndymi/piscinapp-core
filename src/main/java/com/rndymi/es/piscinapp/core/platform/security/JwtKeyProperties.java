package com.rndymi.es.piscinapp.core.platform.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(
        prefix = "piscinapp.security.jwt"
)
public class JwtKeyProperties {

    private String keyStoreBase64;

    private String keyStorePassword;

    private String keyPassword;

    private String keyAlias;

    private String keyId;
}
