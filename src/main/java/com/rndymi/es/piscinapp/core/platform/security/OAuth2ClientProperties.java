package com.rndymi.es.piscinapp.core.platform.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@ConfigurationProperties(
        prefix = "piscinapp.security.oauth2"
)
public class OAuth2ClientProperties {

    private Duration accessTokenTimeToLive =
            Duration.ofMinutes(15);

    private Map<String, Client> clients =
            new LinkedHashMap<>();

    @Getter
    @Setter
    public static class Client {

        private boolean enabled;

        private String clientId;

        private String redirectUri;

        private Set<String> scopes =
                new LinkedHashSet<>();
    }
}
