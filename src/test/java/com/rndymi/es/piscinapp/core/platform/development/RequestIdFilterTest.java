package com.rndymi.es.piscinapp.core.platform.development;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class RequestIdFilterTest {

    private final RequestIdFilter filter =
            new RequestIdFilter();

    @Test
    void shouldGenerateRequestIdWhenMissing()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

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

        assertThat(
                response.getHeader(
                        RequestIdFilter.HEADER_NAME
                )
        )
                .isNotBlank();
    }

    @Test
    void shouldKeepValidIncomingRequestId()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                RequestIdFilter.HEADER_NAME,
                "control-request-123"
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

        assertThat(
                response.getHeader(
                        RequestIdFilter.HEADER_NAME
                )
        )
                .isEqualTo(
                        "control-request-123"
                );
    }

    @Test
    void shouldClearRequestIdFromMdcAfterRequest()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

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

        assertThat(
                org.slf4j.MDC.get(
                        RequestIdFilter.MDC_KEY
                )
        )
                .isNull();
    }
}
