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

        PropertySource<?> properties =
                sources.getFirst();

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
}
