package com.rndymi.es.piscinapp.core.platform;

import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProdDocumentationConfigurationTests {

    @Test
    void shouldDisableOpenApiAndSwaggerInProduction()
            throws Exception {

        PropertySource<?> properties =
                loadProductionProperties();

        assertThat(
                properties.getProperty(
                        "springdoc.api-docs.enabled"
                )
        )
                .isEqualTo(false);

        assertThat(
                properties.getProperty(
                        "springdoc.swagger-ui.enabled"
                )
        )
                .isEqualTo(false);
    }

    @Test
    void shouldRequireExternalProductionIssuer()
            throws Exception {

        PropertySource<?> properties =
                loadProductionProperties();

        assertThat(
                properties.getProperty(
                        "piscinapp.security.issuer"
                )
        )
                .isEqualTo(
                        "${PISCINAPP_SECURITY_ISSUER}"
                );
    }

    @Test
    void shouldUseFrameworkForwardedHeadersInProduction()
            throws Exception {

        PropertySource<?> properties =
                loadProductionProperties();

        assertThat(
                properties.getProperty(
                        "server.forward-headers-strategy"
                )
        )
                .isEqualTo("framework");
    }

    private PropertySource<?> loadProductionProperties()
            throws Exception {

        YamlPropertySourceLoader loader =
                new YamlPropertySourceLoader();

        List<PropertySource<?>> sources =
                loader.load(
                        "application-prod",
                        new ClassPathResource(
                                "application-prod.yml"
                        )
                );

        assertThat(sources)
                .isNotEmpty();

        return sources.getFirst();
    }
}
