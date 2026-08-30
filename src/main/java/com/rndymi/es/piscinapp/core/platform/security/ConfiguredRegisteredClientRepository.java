package com.rndymi.es.piscinapp.core.platform.security;

import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ConfiguredRegisteredClientRepository
        implements RegisteredClientRepository {

    private final Map<String, RegisteredClient>
            clientsById;

    private final Map<String, RegisteredClient>
            clientsByClientId;

    public ConfiguredRegisteredClientRepository(
            Collection<RegisteredClient> clients
    ) {

        this.clientsById =
                new HashMap<>();

        this.clientsByClientId =
                new HashMap<>();

        clients.forEach(
                client -> {
                    clientsById.put(
                            client.getId(),
                            client
                    );

                    clientsByClientId.put(
                            client.getClientId(),
                            client
                    );
                }
        );
    }

    @Override
    public void save(
            RegisteredClient registeredClient
    ) {

        throw new UnsupportedOperationException(
                "Dynamic OAuth2 client registration is not supported"
        );
    }

    @Override
    public RegisteredClient findById(
            String id
    ) {

        return clientsById.get(id);
    }

    @Override
    public RegisteredClient findByClientId(
            String clientId
    ) {

        return clientsByClientId.get(
                clientId
        );
    }
}
