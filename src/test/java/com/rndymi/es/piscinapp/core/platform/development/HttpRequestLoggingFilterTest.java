package com.rndymi.es.piscinapp.core.platform.development;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HttpRequestLoggingFilterTest {

    private final HttpRequestLoggingFilter filter =
            new HttpRequestLoggingFilter();

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void shouldPassRequestThroughFilterChain() throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest(
                        "GET",
                        "/api/v1/me"
                );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain =
                mock(
                        FilterChain.class
                );

        MDC.put(
                RequestIdFilter.MDC_KEY,
                "request-123"
        );

        filter.doFilter(
                request,
                response,
                filterChain
        );

        verify(
                filterChain
        )
                .doFilter(
                        request,
                        response
                );
    }

    @Test
    void shouldHandleBlankRequestUri() throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest(
                        "GET",
                        ""
                );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain =
                mock(
                        FilterChain.class
                );

        filter.doFilter(
                request,
                response,
                filterChain
        );

        verify(
                filterChain
        )
                .doFilter(
                        request,
                        response
                );
    }

    @Test
    void shouldHandleNullRequestUri() throws Exception {

        MockHttpServletRequest request =
                mock(
                        MockHttpServletRequest.class
                );

        when(
                request.getRequestURI()
        )
                .thenReturn(
                        null
                );

        when(
                request.getMethod()
        )
                .thenReturn(
                        "GET"
                );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain =
                mock(
                        FilterChain.class
                );

        filter.doFilter(
                request,
                response,
                filterChain
        );

        verify(
                filterChain
        )
                .doFilter(
                        request,
                        response
                );
    }

    @Test
    void shouldKeepFinallyBehaviorWhenDownstreamFails() throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest(
                        "POST",
                        "/api/v1/test"
                );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain =
                mock(
                        FilterChain.class
                );

        org.mockito.Mockito
                .doThrow(
                        new ServletException(
                                "downstream failure"
                        )
                )
                .when(
                        filterChain
                )
                .doFilter(
                        request,
                        response
                );

        assertThatThrownBy(
                () ->
                        filter.doFilter(
                                request,
                                response,
                                filterChain
                        )
        )
                .isInstanceOf(
                        ServletException.class
                )
                .hasMessageContaining(
                        "downstream failure"
                );
    }
}
