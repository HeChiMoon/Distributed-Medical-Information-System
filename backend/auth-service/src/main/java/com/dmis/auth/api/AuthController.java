package com.dmis.auth.api;

import com.dmis.auth.model.LoginRequest;
import com.dmis.auth.model.LoginResponse;
import com.dmis.auth.service.JwtTokenService;
import com.dmis.common.api.ApiResponse;
import com.dmis.common.api.ModuleInfo;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtTokenService jwtTokenService;

    public AuthController(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = jwtTokenService.issueToken(request.username(), List.of("ADMIN", "DOCTOR"));
        return ApiResponse.ok(new LoginResponse(token, "Bearer", 7200L));
    }

    @GetMapping("/modules")
    public ApiResponse<ModuleInfo> modules() {
        return ApiResponse.ok(new ModuleInfo("auth-service", "authentication", List.of("login", "jwt", "rbac")));
    }
}
