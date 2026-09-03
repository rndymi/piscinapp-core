package com.rndymi.es.piscinapp.core.pools.api;

import com.rndymi.es.piscinapp.core.pools.application.SwimmingPoolService;
import com.rndymi.es.piscinapp.core.pools.domain.SwimmingPool;
import com.rndymi.es.piscinapp.core.pools.persistence.SwimmingPoolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class SwimmingPoolApiIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SwimmingPoolService
            swimmingPoolService;

    @Autowired
    private SwimmingPoolRepository
            swimmingPoolRepository;

    @BeforeEach
    void setUp() {

        swimmingPoolRepository
                .deleteAll();
    }

    @Test
    void shouldAllowAdminToCreateSwimmingPool()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/api/v1/pools"
                        )
                                .with(
                                        adminJwt()
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "name": " Residencial Norte ",
                                          "address": " Calle Example 10, Madrid "
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        header()
                                .string(
                                        "Location",
                                        org.hamcrest.Matchers
                                                .startsWith(
                                                        "/api/v1/pools/"
                                                )
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.name"
                        )
                                .value(
                                        "Residencial Norte"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.address"
                        )
                                .value(
                                        "Calle Example 10, Madrid"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.active"
                        )
                                .value(
                                        true
                                )
                );
    }

    @Test
    void shouldRejectBlankSwimmingPoolName()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/api/v1/pools"
                        )
                                .with(
                                        adminJwt()
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "name": "   ",
                                          "address": "Calle Example 10, Madrid"
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "VALIDATION_ERROR"
                                )
                );
    }

    @Test
    void shouldRejectBlankSwimmingPoolAddress()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/api/v1/pools"
                        )
                                .with(
                                        adminJwt()
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "name": "Residencial Norte",
                                          "address": "   "
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "VALIDATION_ERROR"
                                )
                );
    }

    @Test
    void shouldReturnSwimmingPool()
            throws Exception {

        SwimmingPool pool =
                swimmingPoolService
                        .createPool(
                                "Residencial Norte",
                                "Calle Example 10, Madrid"
                        );

        mockMvc.perform(
                        get(
                                "/api/v1/pools/{id}",
                                pool.getId()
                        )
                                .with(
                                        adminJwt()
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.id"
                        )
                                .value(
                                        pool
                                                .getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.name"
                        )
                                .value(
                                        "Residencial Norte"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.address"
                        )
                                .value(
                                        "Calle Example 10, Madrid"
                                )
                );
    }

    @Test
    void shouldReturnSwimmingPoolNotFound()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/pools/{id}",
                                UUID.randomUUID()
                        )
                                .with(
                                        adminJwt()
                                )
                )
                .andExpect(
                        status().isNotFound()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "POOL_NOT_FOUND"
                                )
                );
    }

    @Test
    void shouldUpdateSwimmingPool()
            throws Exception {

        SwimmingPool pool =
                swimmingPoolService
                        .createPool(
                                "Residencial Norte",
                                "Calle Example 10, Madrid"
                        );

        mockMvc.perform(
                        put(
                                "/api/v1/pools/{id}",
                                pool.getId()
                        )
                                .with(
                                        adminJwt()
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "name": "Hotel Central - Exterior",
                                          "address": "Avenida Central 25, Madrid"
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.id"
                        )
                                .value(
                                        pool
                                                .getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.name"
                        )
                                .value(
                                        "Hotel Central - Exterior"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.address"
                        )
                                .value(
                                        "Avenida Central 25, Madrid"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.active"
                        )
                                .value(
                                        true
                                )
                );
    }

    @Test
    void shouldDeactivateAndReactivateSwimmingPool()
            throws Exception {

        SwimmingPool pool =
                swimmingPoolService
                        .createPool(
                                "Residencial Norte",
                                "Calle Example 10, Madrid"
                        );

        mockMvc.perform(
                        put(
                                "/api/v1/pools/{id}/status",
                                pool.getId()
                        )
                                .with(
                                        adminJwt()
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "active": false
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.active"
                        )
                                .value(
                                        false
                                )
                );

        mockMvc.perform(
                        put(
                                "/api/v1/pools/{id}/status",
                                pool.getId()
                        )
                                .with(
                                        adminJwt()
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "active": true
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.active"
                        )
                                .value(
                                        true
                                )
                );
    }

    @Test
    void shouldReturnPaginatedSwimmingPools()
            throws Exception {

        swimmingPoolService
                .createPool(
                        "Residencial Norte",
                        "Calle Norte 10, Madrid"
                );

        swimmingPoolService
                .createPool(
                        "Hotel Central",
                        "Avenida Centro 20, Madrid"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/pools"
                        )
                                .with(
                                        adminJwt()
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.content",
                                hasSize(
                                        2
                                )
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.page"
                        )
                                .value(
                                        0
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.size"
                        )
                                .value(
                                        20
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.totalElements"
                        )
                                .value(
                                        2
                                )
                );
    }

    @Test
    void shouldFilterSwimmingPoolsByActiveStateAndSearch()
            throws Exception {

        SwimmingPool inactive =
                swimmingPoolService
                        .createPool(
                                "Residencial Norte",
                                "Calle Norte 10, Madrid"
                        );

        swimmingPoolService
                .updateStatus(
                        inactive.getId(),
                        false
                );

        swimmingPoolService
                .createPool(
                        "Hotel Central",
                        "Avenida Centro 20, Madrid"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/pools"
                        )
                                .queryParam(
                                        "active",
                                        "false"
                                )
                                .queryParam(
                                        "search",
                                        "NORTE"
                                )
                                .with(
                                        adminJwt()
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.content",
                                hasSize(
                                        1
                                )
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.content[0].name"
                        )
                                .value(
                                        "Residencial Norte"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.content[0].active"
                        )
                                .value(
                                        false
                                )
                );
    }

    @Test
    void shouldSearchSwimmingPoolsByAddress()
            throws Exception {

        swimmingPoolService
                .createPool(
                        "Residencial Norte",
                        "Calle Alcalá 100, Madrid"
                );

        swimmingPoolService
                .createPool(
                        "Hotel Central",
                        "Gran Vía 20, Madrid"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/pools"
                        )
                                .queryParam(
                                        "search",
                                        "ALCALÁ"
                                )
                                .with(
                                        adminJwt()
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.content",
                                hasSize(
                                        1
                                )
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.content[0].name"
                        )
                                .value(
                                        "Residencial Norte"
                                )
                );
    }

    @Test
    void shouldRejectNegativePage()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/pools"
                        )
                                .queryParam(
                                        "page",
                                        "-1"
                                )
                                .with(
                                        adminJwt()
                                )
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "VALIDATION_ERROR"
                                )
                );
    }

    @Test
    void shouldRejectPageSizeAboveMaximum()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/pools"
                        )
                                .queryParam(
                                        "size",
                                        "101"
                                )
                                .with(
                                        adminJwt()
                                )
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "VALIDATION_ERROR"
                                )
                );
    }

    @Test
    void shouldRejectUnsupportedSort()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/pools"
                        )
                                .queryParam(
                                        "sort",
                                        "maintenanceActivities,asc"
                                )
                                .with(
                                        adminJwt()
                                )
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "VALIDATION_ERROR"
                                )
                );
    }

    @Test
    void shouldRejectSwimmingPoolAdministrationForNormalUser()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/pools"
                        )
                                .with(
                                        userJwt()
                                )
                )
                .andExpect(
                        status().isForbidden()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "ACCESS_DENIED"
                                )
                );
    }

    @Test
    void shouldRequireAuthentication()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/pools"
                        )
                )
                .andExpect(
                        status().isUnauthorized()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "AUTHENTICATION_REQUIRED"
                                )
                );
    }

    private org.springframework.test.web.servlet.request.RequestPostProcessor
    adminJwt() {

        return jwt()
                .jwt(
                        jwt ->
                                jwt.subject(
                                        "admin.user"
                                )
                )
                .authorities(
                        new SimpleGrantedAuthority(
                                "ROLE_ADMIN"
                        )
                );
    }

    private org.springframework.test.web.servlet.request.RequestPostProcessor
    userJwt() {

        return jwt()
                .jwt(
                        jwt ->
                                jwt.subject(
                                        "normal.user"
                                )
                )
                .authorities(
                        new SimpleGrantedAuthority(
                                "ROLE_USER"
                        )
                );
    }
}
