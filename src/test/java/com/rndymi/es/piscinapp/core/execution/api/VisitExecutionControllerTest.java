package com.rndymi.es.piscinapp.core.execution.api;

import com.rndymi.es.piscinapp.core.execution.application.VisitExecutionService;
import com.rndymi.es.piscinapp.core.execution.domain.VisitObservation;
import com.rndymi.es.piscinapp.core.planning.application.VisitExecutionReference;
import com.rndymi.es.piscinapp.core.planning.domain.VisitStatus;
import com.rndymi.es.piscinapp.core.platform.security.AuthenticatedUser;
import com.rndymi.es.piscinapp.core.platform.security.AuthenticatedUserResolver;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VisitExecutionControllerTest {

    private final VisitExecutionService visitExecutionService =
            mock(
                    VisitExecutionService.class
            );

    private final AssignedVisitPageRequestFactory pageRequestFactory =
            mock(
                    AssignedVisitPageRequestFactory.class
            );

    private final AuthenticatedUserResolver authenticatedUserResolver =
            mock(
                    AuthenticatedUserResolver.class
            );

    private final VisitExecutionController controller =
            new VisitExecutionController(
                    visitExecutionService,
                    pageRequestFactory,
                    authenticatedUserResolver
            );

    private final Authentication authentication =
            mock(
                    Authentication.class
            );

    @Test
    void shouldUseAdminExecutionDetailPath() {

        UUID visitId =
                UUID.randomUUID();

        VisitExecutionReference reference =
                visitReference(
                        visitId
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
                visitExecutionService.getExecutionDetailForAdmin(
                        visitId
                )
        )
                .thenReturn(
                        reference
                );

        var response =
                controller.getExecutionDetail(
                        visitId,
                        authentication
                );

        assertThat(
                response.id()
        )
                .isEqualTo(
                        visitId
                );

        verify(
                visitExecutionService
        )
                .getExecutionDetailForAdmin(
                        visitId
                );

        verify(
                visitExecutionService,
                never()
        )
                .getExecutionDetailForAssignedActor(
                        visitId,
                        "admin"
                );
    }

    @Test
    void shouldUseAssignedActorExecutionDetailPath() {

        UUID visitId =
                UUID.randomUUID();

        VisitExecutionReference reference =
                visitReference(
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
                visitExecutionService.getExecutionDetailForAssignedActor(
                        visitId,
                        "worker"
                )
        )
                .thenReturn(
                        reference
                );

        var response =
                controller.getExecutionDetail(
                        visitId,
                        authentication
                );

        assertThat(
                response.id()
        )
                .isEqualTo(
                        visitId
                );

        verify(
                visitExecutionService
        )
                .getExecutionDetailForAssignedActor(
                        visitId,
                        "worker"
                );
    }

    @Test
    void shouldUseAdminObservationPath() {

        UUID visitId =
                UUID.randomUUID();

        VisitObservation observation =
                observation(
                        visitId
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
                visitExecutionService.getObservationsForAdmin(
                        visitId
                )
        )
                .thenReturn(
                        List.of(
                                observation
                        )
                );

        var response =
                controller.getObservations(
                        visitId,
                        authentication
                );

        assertThat(
                response
        )
                .hasSize(
                        1
                );

        assertThat(
                response.getFirst().id()
        )
                .isEqualTo(
                        observation.getId()
                );

        verify(
                visitExecutionService
        )
                .getObservationsForAdmin(
                        visitId
                );
    }

    @Test
    void shouldUseAssignedActorObservationPath() {

        UUID visitId =
                UUID.randomUUID();

        VisitObservation observation =
                observation(
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
                visitExecutionService.getObservationsForAssignedActor(
                        visitId,
                        "worker"
                )
        )
                .thenReturn(
                        List.of(
                                observation
                        )
                );

        var response =
                controller.getObservations(
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
                visitExecutionService
        )
                .getObservationsForAssignedActor(
                        visitId,
                        "worker"
                );
    }

    private VisitExecutionReference visitReference(
            UUID visitId
    ) {

        return new VisitExecutionReference(
                visitId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDate.now(),
                LocalTime.of(
                        10,
                        0
                ),
                VisitStatus.PLANNED,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                List.of()
        );
    }

    private VisitObservation observation(
            UUID visitId
    ) {

        return new VisitObservation(
                UUID.randomUUID(),
                visitId,
                "Filter checked",
                Instant.now(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
    }
}
