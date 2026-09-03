package com.rndymi.es.piscinapp.core.identity.bootstrap;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(
        prefix = "piscinapp.bootstrap.owner"
)
public class BootstrapOwnerProperties {

    private String username;

    private String password;
}
