package com.rndymi.es.piscinapp.core.identity.api;

import com.rndymi.es.piscinapp.core.identity.api.dto.CreateUserAccountRequest;
import com.rndymi.es.piscinapp.core.identity.api.dto.SetUserPasswordRequest;
import com.rndymi.es.piscinapp.core.identity.api.dto.UpdateUserRolesRequest;
import com.rndymi.es.piscinapp.core.identity.api.dto.UpdateUserStatusRequest;
import com.rndymi.es.piscinapp.core.identity.api.dto.UserAccountResponse;
import com.rndymi.es.piscinapp.core.identity.application.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(
        "/api/v1/users"
)
public class UserAccountController {

    private final UserAccountService
            userAccountService;

    public UserAccountController(
            UserAccountService userAccountService
    ) {

        this.userAccountService =
                userAccountService;
    }

    @PostMapping
    @Operation(
            summary =
                    "Create a PiscinApp security account"
    )
    public ResponseEntity<UserAccountResponse>
    createAccount(
            @Valid
            @RequestBody
            CreateUserAccountRequest request
    ) {

        UserAccountResponse response =
                UserAccountResponse.from(
                        userAccountService
                                .createAccount(
                                        request.username(),
                                        request.password(),
                                        request.enabled(),
                                        request.roles()
                                )
                );

        return ResponseEntity
                .created(
                        URI.create(
                                "/api/v1/users/"
                                        + response.id()
                        )
                )
                .body(
                        response
                );
    }

    @GetMapping
    @Operation(
            summary =
                    "List PiscinApp security accounts"
    )
    public List<UserAccountResponse>
    listAccounts() {

        return userAccountService
                .listAccounts()
                .stream()
                .map(
                        UserAccountResponse::from
                )
                .toList();
    }

    @GetMapping(
            "/{id}"
    )
    @Operation(
            summary =
                    "Return one PiscinApp security account"
    )
    public UserAccountResponse getAccount(
            @PathVariable
            UUID id
    ) {

        return UserAccountResponse.from(
                userAccountService
                        .getAccount(
                                id
                        )
        );
    }

    @PutMapping(
            "/{id}/roles"
    )
    @Operation(
            summary =
                    "Replace an account security-role set"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Account roles replaced"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid role set or malformed request"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "ADMIN role required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Protected Owner account or last enabled administrator cannot have its roles changed"
            )
    })
    public UserAccountResponse replaceRoles(
            @PathVariable
            UUID id,
            @Valid
            @RequestBody
            UpdateUserRolesRequest request
    ) {

        return UserAccountResponse.from(
                userAccountService
                        .replaceRoles(
                                id,
                                request.roles()
                        )
        );
    }

    @PutMapping(
            "/{id}/status"
    )
    @Operation(
            summary =
                    "Enable or disable an account"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Account status updated"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or malformed request"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "ADMIN role required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Protected Owner account or last enabled administrator cannot be disabled"
            )
    })
    public UserAccountResponse updateStatus(
            @PathVariable
            UUID id,
            @Valid
            @RequestBody
            UpdateUserStatusRequest request
    ) {

        return UserAccountResponse.from(
                userAccountService
                        .updateStatus(
                                id,
                                request.enabled()
                        )
        );
    }

    @PutMapping(
            "/{id}/password"
    )
    @Operation(
            summary =
                    "Set a new password for an account"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Account password updated"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Password does not satisfy the password policy or request is malformed"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "ADMIN role required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Protected Owner password cannot be replaced through account administration"
            )
    })
    public ResponseEntity<Void>
    setPassword(
            @PathVariable
            UUID id,
            @Valid
            @RequestBody
            SetUserPasswordRequest request
    ) {

        userAccountService
                .setPasswordAsAdmin(
                        id,
                        request.password()
                );

        return ResponseEntity
                .noContent()
                .build();
    }
}
