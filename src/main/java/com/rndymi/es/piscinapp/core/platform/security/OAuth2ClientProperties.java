package com.rndymi.es.piscinapp.core.platform.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@ConfigurationProperties(
        prefix = "piscinapp.security.oauth2"
)
public class OAuth2ClientProperties {

    private Duration accessTokenTimeToLive =
            Duration.ofMinutes(15);

    private Map<String, Client> clients =
            new LinkedHashMap<>();

    public Duration getAccessTokenTimeToLive() {

        return accessTokenTimeToLive;
    }

    public void setAccessTokenTimeToLive(
            Duration accessTokenTimeToLive
    ) {

        this.accessTokenTimeToLive =
                accessTokenTimeToLive;
    }

    public Map<String, Client> getClients() {

        return clients;
    }

    public void setClients(
            Map<String, Client> clients
    ) {

        this.clients =
                clients;
    }

    public static class Client {

        private boolean enabled;
        private String clientId;
        private String redirectUri;

        private Set<String> scopes =
                new LinkedHashSet<>();

        public boolean isEnabled() {

            return enabled;
        }

        public void setEnabled(
                boolean enabled
        ) {

            this.enabled =
                    enabled;
        }

        public String getClientId() {

            return clientId;
        }

        public void setClientId(
                String clientId
        ) {

            this.clientId =
                    clientId;
        }

        public String getRedirectUri() {

            return redirectUri;
        }

        public void setRedirectUri(
                String redirectUri
        ) {

            this.redirectUri =
                    redirectUri;
        }

        public Set<String> getScopes() {

            return scopes;
        }

        public void setScopes(
                Set<String> scopes
        ) {

            this.scopes =
                    scopes;
        }
    }
}
