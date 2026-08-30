package com.rndymi.es.piscinapp.core.platform.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/security-test")
public class SecurityValidationController {

    @GetMapping("/user")
    Map<String, String> user() {

        return Map.of(
                "access",
                "USER"
        );
    }

    @GetMapping("/admin")
    Map<String, String> admin() {

        return Map.of(
                "access",
                "ADMIN"
        );
    }
}
