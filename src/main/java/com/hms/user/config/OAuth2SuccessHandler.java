package com.hms.user.config;

import com.hms.user.entity.Role;
import com.hms.user.entity.enums.RoleName;
import com.hms.user.entity.User;
import com.hms.user.repo.RoleRepository;
import com.hms.user.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * On successful OAuth2 login:
 *  - Extract email & profile info from the OAuth2 provider (Google).
 *  - Create the user if missing (default role PATIENT).
 *  - Generate a JWT and redirect to the frontend with token + expiry.
 */
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Value("${hms.jwt.expiration-ms}")
    private long jwtExpiryMs;

    /**
     * Frontend callback URL that receives token & expiry.
     * Example: http://localhost:3000/oauth2/callback
     */
    @Value("${hms.oauth2.redirect-url:http://localhost:3000/oauth2/callback}")
    private String redirectUrl;

    public OAuth2SuccessHandler(JwtService jwtService,
                                UserRepository userRepository,
                                RoleRepository roleRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // Common Google claims: email, name, given_name, family_name
        String email = value(oAuth2User, "email");
        if (email == null || email.isBlank()) {
            // Some providers might not return email depending on scopes.
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email not provided by OAuth2 provider.");
            return;
        }

        String givenName = alt(oAuth2User, "given_name", "first_name", "name");
        String familyName = alt(oAuth2User, "family_name", "last_name", null);

        // Upsert user
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User u = new User();
            u.setEmail(email);
            // Set a random password (not used for OAuth sign-ins).
            u.setPassword("{noop}" + UUID.randomUUID()); // won't be used with form login; stored safely if encoded later
            return u;
        });

        if (givenName != null && !givenName.isBlank()) user.setFirstName(givenName);
        if (familyName != null && !familyName.isBlank()) user.setLastName(familyName);

        // Ensure PATIENT role exists on user (idempotent)
        Role patientRole = roleRepository.findByName(RoleName.PATIENT)
                .orElseThrow(() -> new IllegalStateException("Role PATIENT not seeded"));
        if (user.getRoles() == null || user.getRoles().isEmpty() ||
                user.getRoles().stream().noneMatch(r -> r.getName() == RoleName.PATIENT)) {
            user.getRoles().add(patientRole);
        }

        User saved = userRepository.save(user);

        // Build Spring Security UserDetails for JWT generation
        Set<SimpleGrantedAuthority> authorities = saved.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName().name()))
                .collect(Collectors.toSet());

        UserDetails principal = org.springframework.security.core.userdetails.User
                .withUsername(saved.getEmail())
                .password(saved.getPassword() == null ? "" : saved.getPassword())
                .authorities(authorities)
                .build();

        String token = jwtService.generateToken(principal);

        // Redirect with token & expiry (URL-encoded)
        String url = redirectUrl
                + "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8)
                + "&expiresIn=" + jwtExpiryMs;

        response.setStatus(HttpServletResponse.SC_FOUND);
        response.sendRedirect(url);
    }

    // ------- helpers -------

    private static String value(OAuth2User user, String key) {
        Object v = user.getAttributes().get(key);
        return v == null ? null : String.valueOf(v);
    }

    private static String alt(OAuth2User user, String primary, String fallback, String fallback2) {
        String v = value(user, primary);
        if (v != null && !v.isBlank()) return v;
        if (fallback != null) {
            v = value(user, fallback);
            if (v != null && !v.isBlank()) return v;
        }
        if (fallback2 != null) {
            v = value(user, fallback2);
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }
}
