package com.example.bilisimgarajitask.auth;

import com.example.bilisimgarajitask.common.Jwt.JwtTokenService;
import com.example.bilisimgarajitask.common.NotFoundException;
import com.example.bilisimgarajitask.common.security.SecurityUtils;
import com.example.bilisimgarajitask.user.Role;
import com.example.bilisimgarajitask.user.User;
import com.example.bilisimgarajitask.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtTokenService jwt;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req){
        User user = userRepo.findByEmailIgnoreCase(req.email())
                .orElseThrow(() -> new NotFoundException("User not found: " + req.email()));

        if (!user.isActive() || !encoder.matches(req.password(), user.getPassword())) {
            return ResponseEntity.status(401).build();
        }

        String access = jwt.generateAccessToken(user);
        String refresh = jwt.generateRefreshToken(user);
        return ResponseEntity.ok(new AuthResponse(
                access,
                0L,
                refresh,
                0L,
                "Bearer"
        ));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest req){
        var jws = jwt.parse(req.refreshToken());
        if (!"refresh".equals(String.valueOf(jws.getBody().get("typ")))) {
            return ResponseEntity.status(401).build();
        }

        String email = jws.getBody().getSubject();
        User user = userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException("User not found: " + email));
        if (!user.isActive()) {
            return ResponseEntity.status(401).build();
        }

        String access = jwt.generateAccessToken(user);
        String refresh = jwt.generateRefreshToken(user);

        return ResponseEntity.ok(new AuthResponse(access, 0L, refresh, 0L, "Bearer"));
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me() {
        String email = SecurityUtils.getCurrentUsername().orElse(null);
        if (email == null) return ResponseEntity.status(401).build();

        User u = userRepo.findByEmailIgnoreCase(email).orElse(null);
        if (u == null || !u.isActive()) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(new MeResponse(
                u.getId(),
                u.getEmail(),
                u.getFirstName(),
                u.getLastName(),
                u.getRole(),
                u.getProfileId(),
                u.getOrganization() != null ? u.getOrganization().getId() : null,
                u.getOrganization() != null ? u.getOrganization().getName() : null,
                u.getClassroom() != null ? u.getClassroom().getId() : null,
                u.getClassroom() != null ? u.getClassroom().getName() : null
        ));
    }

    @PostMapping("/user-register")
    public ResponseEntity<AuthResponse> registerFirstSuperAdmin(
            @Valid @RequestBody UserRegisterRequest req
    ) {
        if (userRepo.existsByRole(Role.SUPER_ADMIN)) {
            return ResponseEntity.status(403).build();
        }


        if (userRepo.existsByEmailIgnoreCase(req.email())) {
            throw new DataIntegrityViolationException("Email already in use");
        }

        User u = new User();
        u.setEmail(req.email().trim());
        u.setPassword(encoder.encode(req.password()));
        u.setFirstName(req.firstName().trim());
        u.setLastName(req.lastName().trim());
        u.setRole(Role.SUPER_ADMIN);
        u.setProfileId(0);
        u.setActive(true);

        u = userRepo.save(u);

        String access  = jwt.generateAccessToken(u);
        String refresh = jwt.generateRefreshToken(u);

        return ResponseEntity.created(URI.create("/api/v1/users/" + u.getId()))
                .body(new AuthResponse(access, 0L, refresh, 0L, "Bearer"));
    }
}
