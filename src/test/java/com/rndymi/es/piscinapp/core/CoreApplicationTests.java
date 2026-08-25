package com.rndymi.es.piscinapp.core;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class CoreApplicationTests {

    @Autowired
    private Environment environment;

    @Autowired
    private DataSource dataSource;

    @Test
    void shouldLoadApplicationContextWithTestProfile() {
        assertThat(environment.getActiveProfiles())
                .contains("test");

        assertThat(dataSource)
                .isNotNull();
    }

    @Test
    void shouldUseLocalPostgreSqlConfiguration() {
        String datasourceUrl =
                environment.getProperty("spring.datasource.url");

        assertThat(datasourceUrl)
                .isEqualTo(
                        "jdbc:postgresql://localhost:5432/piscinappdb"
                );

        assertThat(datasourceUrl)
                .doesNotContain("neon");
    }
}
