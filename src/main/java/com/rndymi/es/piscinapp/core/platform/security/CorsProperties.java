package com.rndymi.es.piscinapp.core.platform.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@ConfigurationProperties(
        prefix = "piscinapp.security.cors"
)
public class CorsProperties {

    private Set<String> allowedOrigins =
            new LinkedHashSet<>();

    private Set<String> allowedMethods =
            new LinkedHashSet<>(
                    Set.of(
                            "GET",
                            "POST",
                            "PUT",
                            "PATCH",
                            "DELETE",
                            "OPTIONS"
                    )
            );

    private Set<String> allowedHeaders =
            new LinkedHashSet<>(
                    Set.of(
                            "Authorization",
                            "Content-Type",
                            "Accept"
                    )
            );

    private boolean allowCredentials =
            false;

    private Duration maxAge =
            Duration.ofHours(1);
}
