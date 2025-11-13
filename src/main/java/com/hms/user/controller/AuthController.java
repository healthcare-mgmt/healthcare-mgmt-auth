package com.hms.user.controller;

import com.hms.user.dto.JwtResponse;
import com.hms.user.dto.LoginRequest;
import com.hms.user.dto.SignupRequest;
import com.hms.user.dto.UserResponse;
import com.hms.user.service.AuthService;
import com.hms.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Value("${hms.jwt.expiration-ms}")
    private long jwtExpiryMs;

    public AuthController(AuthService authService,
                          UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request, jwtExpiryMs));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        return ResponseEntity.ok(userService.getProfile());
    }

    // Optional simple profile update (first/last name only)
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMe(@RequestBody UserResponse patch) {
        return ResponseEntity.ok(
                userService.updateProfile(patch.getFirstName(), patch.getLastName())
        );
    }
}
