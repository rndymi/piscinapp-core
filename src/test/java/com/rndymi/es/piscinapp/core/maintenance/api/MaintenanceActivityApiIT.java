package com.rndymi.es.piscinapp.core.maintenance.api;

import com.rndymi.es.piscinapp.core.maintenance.application.MaintenanceActivityService;
import com.rndymi.es.piscinapp.core.maintenance.application.PoolMaintenanceConfigurationService;
import com.rndymi.es.piscinapp.core.maintenance.domain.MaintenanceActivity;
import com.rndymi.es.piscinapp.core.maintenance.persistence.MaintenanceActivityRepository;
import com.rndymi.es.piscinapp.core.maintenance.persistence.PoolMaintenanceActivityRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class MaintenanceActivityApiIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MaintenanceActivityService
            maintenanceActivityService;

    @Autowired
    private PoolMaintenanceConfigurationService
            configurationService;

    @Autowired
    private SwimmingPoolService
            swimmingPoolService;

    @Autowired
    private MaintenanceActivityRepository
            maintenanceActivityRepository;

    @Autowired
    private PoolMaintenanceActivityRepository
            configurationRepository;

    @Autowired
    private SwimmingPoolRepository
            swimmingPoolRepository;

    @BeforeEach
    void setUp() {

        configurationRepository
                .deleteAll();

        maintenanceActivityRepository
                .deleteAll();

        swimmingPoolRepository
                .deleteAll();
    }

    @Test
    void shouldAllowAdminToCreateMaintenanceActivity()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/api/v1/maintenance-activities"
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
                                          "name": " Filter inspection ",
                                          "description": " Check the filter condition. "
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
                                                        "/api/v1/maintenance-activities/"
                                                )
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.name"
                        )
                                .value(
                                        "Filter inspection"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.description"
                        )
                                .value(
                                        "Check the filter condition."
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
    void shouldNormalizeBlankDescriptionToNull()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/api/v1/maintenance-activities"
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
                                          "name": "Surface cleaning",
                                          "description": "   "
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath(
                                "$.description"
                        )
                                .doesNotExist()
                );
    }

    @Test
    void shouldRejectBlankMaintenanceActivityName()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/api/v1/maintenance-activities"
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
                                          "description": "Description"
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
    void shouldReturnMaintenanceActivity()
            throws Exception {

        MaintenanceActivity activity =
                maintenanceActivityService
                        .createActivity(
                                "Filter inspection",
                                "Check filter condition"
                        );

        mockMvc.perform(
                        get(
                                "/api/v1/maintenance-activities/{id}",
                                activity.getId()
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
                                        activity
                                                .getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.name"
                        )
                                .value(
                                        "Filter inspection"
                                )
                );
    }

    @Test
    void shouldReturnMaintenanceActivityNotFound()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/maintenance-activities/{id}",
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
                                        "MAINTENANCE_ACTIVITY_NOT_FOUND"
                                )
                );
    }

    @Test
    void shouldUpdateMaintenanceActivity()
            throws Exception {

        MaintenanceActivity activity =
                maintenanceActivityService
                        .createActivity(
                                "Filter inspection",
                                null
                        );

        mockMvc.perform(
                        put(
                                "/api/v1/maintenance-activities/{id}",
                                activity.getId()
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
                                          "name": "Water quality check",
                                          "description": "Measure and review water quality."
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
                                        activity
                                                .getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.name"
                        )
                                .value(
                                        "Water quality check"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.description"
                        )
                                .value(
                                        "Measure and review water quality."
                                )
                );
    }

    @Test
    void shouldDeactivateAndReactivateMaintenanceActivity()
            throws Exception {

        MaintenanceActivity activity =
                maintenanceActivityService
                        .createActivity(
                                "Filter inspection",
                                null
                        );

        mockMvc.perform(
                        put(
                                "/api/v1/maintenance-activities/{id}/status",
                                activity.getId()
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
                                "/api/v1/maintenance-activities/{id}/status",
                                activity.getId()
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
    void shouldReturnPaginatedMaintenanceActivities()
            throws Exception {

        maintenanceActivityService
                .createActivity(
                        "Filter inspection",
                        null
                );

        maintenanceActivityService
                .createActivity(
                        "Surface cleaning",
                        null
                );

        mockMvc.perform(
                        get(
                                "/api/v1/maintenance-activities"
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
    void shouldFilterMaintenanceActivitiesByActiveStateAndSearch()
            throws Exception {

        MaintenanceActivity inactive =
                maintenanceActivityService
                        .createActivity(
                                "Filter inspection",
                                "Inspect filtration equipment"
                        );

        maintenanceActivityService
                .updateStatus(
                        inactive.getId(),
                        false
                );

        maintenanceActivityService
                .createActivity(
                        "Surface cleaning",
                        "Clean pool surfaces"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/maintenance-activities"
                        )
                                .queryParam(
                                        "active",
                                        "false"
                                )
                                .queryParam(
                                        "search",
                                        "FILTRATION"
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
                                        "Filter inspection"
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
    void shouldRejectMaintenanceActivityPageSizeAboveMaximum()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/maintenance-activities"
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
    void shouldRejectUnsupportedMaintenanceActivitySort()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/maintenance-activities"
                        )
                                .queryParam(
                                        "sort",
                                        "description,asc"
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
    void shouldConfigureMaintenanceActivityForSwimmingPool()
            throws Exception {

        SwimmingPool pool =
                createPool();

        MaintenanceActivity activity =
                createActivity();

        mockMvc.perform(
                        put(
                                "/api/v1/pools/{poolId}/maintenance-activities/{activityId}",
                                pool.getId(),
                                activity.getId()
                        )
                                .with(
                                        adminJwt()
                                )
                )
                .andExpect(
                        status().isNoContent()
                );
    }

    @Test
    void shouldRejectDuplicatedPoolMaintenanceConfiguration()
            throws Exception {

        SwimmingPool pool =
                createPool();

        MaintenanceActivity activity =
                createActivity();

        configurationService
                .configure(
                        pool.getId(),
                        activity.getId()
                );

        mockMvc.perform(
                        put(
                                "/api/v1/pools/{poolId}/maintenance-activities/{activityId}",
                                pool.getId(),
                                activity.getId()
                        )
                                .with(
                                        adminJwt()
                                )
                )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "POOL_MAINTENANCE_ACTIVITY_CONFLICT"
                                )
                );
    }

    @Test
    void shouldReturnPoolNotFoundWhenConfiguringActivity()
            throws Exception {

        MaintenanceActivity activity =
                createActivity();

        mockMvc.perform(
                        put(
                                "/api/v1/pools/{poolId}/maintenance-activities/{activityId}",
                                UUID.randomUUID(),
                                activity.getId()
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
    void shouldReturnMaintenanceActivityNotFoundWhenConfiguringPool()
            throws Exception {

        SwimmingPool pool =
                createPool();

        mockMvc.perform(
                        put(
                                "/api/v1/pools/{poolId}/maintenance-activities/{activityId}",
                                pool.getId(),
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
                                        "MAINTENANCE_ACTIVITY_NOT_FOUND"
                                )
                );
    }

    @Test
    void shouldRejectNewConfigurationForInactiveSwimmingPool()
            throws Exception {

        SwimmingPool pool =
                createPool();

        MaintenanceActivity activity =
                createActivity();

        swimmingPoolService
                .updateStatus(
                        pool.getId(),
                        false
                );

        mockMvc.perform(
                        put(
                                "/api/v1/pools/{poolId}/maintenance-activities/{activityId}",
                                pool.getId(),
                                activity.getId()
                        )
                                .with(
                                        adminJwt()
                                )
                )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "RESOURCE_INACTIVE"
                                )
                );
    }

    @Test
    void shouldRejectNewConfigurationForInactiveMaintenanceActivity()
            throws Exception {

        SwimmingPool pool =
                createPool();

        MaintenanceActivity activity =
                createActivity();

        maintenanceActivityService
                .updateStatus(
                        activity.getId(),
                        false
                );

        mockMvc.perform(
                        put(
                                "/api/v1/pools/{poolId}/maintenance-activities/{activityId}",
                                pool.getId(),
                                activity.getId()
                        )
                                .with(
                                        adminJwt()
                                )
                )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "RESOURCE_INACTIVE"
                                )
                );
    }

    @Test
    void shouldListConfiguredMaintenanceActivitiesForSwimmingPool()
            throws Exception {

        SwimmingPool pool =
                createPool();

        MaintenanceActivity first =
                maintenanceActivityService
                        .createActivity(
                                "Filter inspection",
                                "Inspect filter"
                        );

        MaintenanceActivity second =
                maintenanceActivityService
                        .createActivity(
                                "Surface cleaning",
                                "Clean surface"
                        );

        configurationService
                .configure(
                        pool.getId(),
                        first.getId()
                );

        configurationService
                .configure(
                        pool.getId(),
                        second.getId()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/pools/{poolId}/maintenance-activities",
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
                                "$.content",
                                hasSize(
                                        2
                                )
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
    void shouldFilterConfiguredActivities()
            throws Exception {

        SwimmingPool pool =
                createPool();

        MaintenanceActivity inactive =
                maintenanceActivityService
                        .createActivity(
                                "Filter inspection",
                                "Inspect filter"
                        );

        MaintenanceActivity active =
                maintenanceActivityService
                        .createActivity(
                                "Surface cleaning",
                                "Clean surface"
                        );

        configurationService
                .configure(
                        pool.getId(),
                        inactive.getId()
                );

        configurationService
                .configure(
                        pool.getId(),
                        active.getId()
                );

        maintenanceActivityService
                .updateStatus(
                        inactive.getId(),
                        false
                );

        mockMvc.perform(
                        get(
                                "/api/v1/pools/{poolId}/maintenance-activities",
                                pool.getId()
                        )
                                .queryParam(
                                        "active",
                                        "false"
                                )
                                .queryParam(
                                        "search",
                                        "FILTER"
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
                                "$.content[0].id"
                        )
                                .value(
                                        inactive
                                                .getId()
                                                .toString()
                                )
                );
    }

    @Test
    void shouldPreserveExistingConfigurationAfterDeactivation()
            throws Exception {

        SwimmingPool pool =
                createPool();

        MaintenanceActivity activity =
                createActivity();

        configurationService
                .configure(
                        pool.getId(),
                        activity.getId()
                );

        swimmingPoolService
                .updateStatus(
                        pool.getId(),
                        false
                );

        maintenanceActivityService
                .updateStatus(
                        activity.getId(),
                        false
                );

        mockMvc.perform(
                        get(
                                "/api/v1/pools/{poolId}/maintenance-activities",
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
                                "$.content",
                                hasSize(
                                        1
                                )
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.content[0].id"
                        )
                                .value(
                                        activity
                                                .getId()
                                                .toString()
                                )
                );
    }

    @Test
    void shouldRemoveApplicabilityWithoutDeletingMasterData()
            throws Exception {

        SwimmingPool pool =
                createPool();

        MaintenanceActivity activity =
                createActivity();

        configurationService
                .configure(
                        pool.getId(),
                        activity.getId()
                );

        mockMvc.perform(
                        delete(
                                "/api/v1/pools/{poolId}/maintenance-activities/{activityId}",
                                pool.getId(),
                                activity.getId()
                        )
                                .with(
                                        adminJwt()
                                )
                )
                .andExpect(
                        status().isNoContent()
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
                );

        mockMvc.perform(
                        get(
                                "/api/v1/maintenance-activities/{id}",
                                activity.getId()
                        )
                                .with(
                                        adminJwt()
                                )
                )
                .andExpect(
                        status().isOk()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/pools/{poolId}/maintenance-activities",
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
                                "$.content",
                                hasSize(
                                        0
                                )
                        )
                );
    }

    @Test
    void shouldRejectMaintenanceAdministrationForNormalUser()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/maintenance-activities"
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
    void shouldRejectPoolMaintenanceConfigurationForNormalUser()
            throws Exception {

        SwimmingPool pool =
                createPool();

        MaintenanceActivity activity =
                createActivity();

        mockMvc.perform(
                        put(
                                "/api/v1/pools/{poolId}/maintenance-activities/{activityId}",
                                pool.getId(),
                                activity.getId()
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
    void shouldRequireAuthenticationForMaintenanceAdministration()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/maintenance-activities"
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

    @Test
    void shouldRequireAuthenticationForPoolMaintenanceConfiguration()
            throws Exception {

        SwimmingPool pool =
                createPool();

        MaintenanceActivity activity =
                createActivity();

        mockMvc.perform(
                        put(
                                "/api/v1/pools/{poolId}/maintenance-activities/{activityId}",
                                pool.getId(),
                                activity.getId()
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

    private SwimmingPool createPool() {

        return swimmingPoolService
                .createPool(
                        "Residencial Norte",
                        "Calle Example 10, Madrid"
                );
    }

    private MaintenanceActivity createActivity() {

        return maintenanceActivityService
                .createActivity(
                        "Filter inspection",
                        "Check filter condition"
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
