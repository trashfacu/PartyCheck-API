package com.partyCheck.Backend.auth;

import com.partyCheck.Backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register (@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate (@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));

    }
    @PostMapping("/admin/{email}/toggle")
    public ResponseEntity<AuthResponse> toggleAdminPermission (@PathVariable String email){
        authenticationService.toggleAdminAccess(email);
        return ResponseEntity.ok().build();
    }

}
