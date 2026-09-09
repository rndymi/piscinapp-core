package com.rndymi.es.piscinapp.core.incidents.api;

import com.rndymi.es.piscinapp.core.incidents.application.IncidentService;
import com.rndymi.es.piscinapp.core.incidents.domain.Incident;
import com.rndymi.es.piscinapp.core.platform.security.AuthenticatedUser;
import com.rndymi.es.piscinapp.core.platform.security.AuthenticatedUserResolver;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IncidentControllerTest {

    private final IncidentService incidentService =
            mock(
                    IncidentService.class
            );

    private final IncidentPageRequestFactory pageRequestFactory =
            mock(
                    IncidentPageRequestFactory.class
            );

    private final AuthenticatedUserResolver authenticatedUserResolver =
            mock(
                    AuthenticatedUserResolver.class
            );

    private final IncidentController controller =
            new IncidentController(
                    incidentService,
                    pageRequestFactory,
                    authenticatedUserResolver
            );

    private final Authentication authentication =
            mock(
                    Authentication.class
            );

    @Test
    void shouldGetIncidentForAssignedActor() {

        UUID incidentId =
                UUID.randomUUID();

        Incident incident =
                incident(
                        incidentId
                );

        when(
                authenticatedUserResolver.resolve(
                        authentication
                )
        )
                .thenReturn(
                        new AuthenticatedUser(
                                "worker",
                                false
                        )
                );

        when(
                incidentService.getIncidentForActor(
                        incidentId,
                        "worker",
                        false
                )
        )
                .thenReturn(
                        incident
                );

        var response =
                controller.getIncident(
                        incidentId,
                        authentication
                );

        assertThat(
                response.id()
        )
                .isEqualTo(
                        incidentId
                );

        verify(
                incidentService
        )
                .getIncidentForActor(
                        incidentId,
                        "worker",
                        false
                );
    }

    @Test
    void shouldGetIncidentForAdmin() {

        UUID incidentId =
                UUID.randomUUID();

        Incident incident =
                incident(
                        incidentId
                );

        when(
                authenticatedUserResolver.resolve(
                        authentication
                )
        )
                .thenReturn(
                        new AuthenticatedUser(
                                "admin",
                                true
                        )
                );

        when(
                incidentService.getIncidentForActor(
                        incidentId,
                        "admin",
                        true
                )
        )
                .thenReturn(
                        incident
                );

        var response =
                controller.getIncident(
                        incidentId,
                        authentication
                );

        assertThat(
                response.id()
        )
                .isEqualTo(
                        incidentId
                );

        verify(
                incidentService
        )
                .getIncidentForActor(
                        incidentId,
                        "admin",
                        true
                );
    }

    @Test
    void shouldGetVisitIncidentsForAssignedActor() {

        UUID visitId =
                UUID.randomUUID();

        Incident incident =
                incident(
                        UUID.randomUUID(),
                        visitId
                );

        when(
                authenticatedUserResolver.resolve(
                        authentication
                )
        )
                .thenReturn(
                        new AuthenticatedUser(
                                "worker",
                                false
                        )
                );

        when(
                incidentService.getVisitIncidentsForActor(
                        visitId,
                        "worker",
                        false
                )
        )
                .thenReturn(
                        List.of(
                                incident
                        )
                );

        var response =
                controller.getVisitIncidents(
                        visitId,
                        authentication
                );

        assertThat(
                response
        )
                .hasSize(
                        1
                );

        verify(
                incidentService
        )
                .getVisitIncidentsForActor(
                        visitId,
                        "worker",
                        false
                );
    }

    private Incident incident(
            UUID incidentId
    ) {

        return incident(
                incidentId,
                UUID.randomUUID()
        );
    }

    private Incident incident(
            UUID incidentId,
            UUID visitId
    ) {

        return new Incident(
                incidentId,
                visitId,
                "Pump cannot start",
                Instant.now(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
    }
}
