package com.rndymi.es.piscinapp.core.identity.api;

import com.rndymi.es.piscinapp.core.identity.api.dto.ChangeOwnPasswordRequest;
import com.rndymi.es.piscinapp.core.identity.api.dto.UserAccountResponse;
import com.rndymi.es.piscinapp.core.identity.application.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        "/api/v1/me"
)
public class CurrentUserController {

    private final UserAccountService
            userAccountService;

    public CurrentUserController(
            UserAccountService userAccountService
    ) {

        this.userAccountService =
                userAccountService;
    }

    @GetMapping
    @Operation(
            summary =
                    "Return the current authenticated PiscinApp account"
    )
    public UserAccountResponse getCurrentAccount(
            Authentication authentication
    ) {

        return UserAccountResponse.from(
                userAccountService
                        .getCurrentAccount(
                                authentication
                                        .getName()
                        )
        );
    }

    @PutMapping(
            "/password"
    )
    @Operation(
            summary =
                    "Change the current account password"
    )
    public ResponseEntity<Void>
    changeOwnPassword(
            Authentication authentication,
            @Valid
            @RequestBody
            ChangeOwnPasswordRequest request
    ) {

        userAccountService
                .changeOwnPassword(
                        authentication
                                .getName(),
                        request
                                .currentPassword(),
                        request
                                .newPassword()
                );

        return ResponseEntity
                .noContent()
                .build();
    }
}
