package com.hms.user.service;

import com.hms.user.dto.UserResponse;
import com.hms.user.entity.User;
import com.hms.user.repo.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthService authService;

    public UserService(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    /** Cached lookup by email (used by /me and elsewhere). */
    @Cacheable(value = "users", key = "#email")
    public UserResponse getUserByEmail(String email) {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found: " + email));
        return authService.toResponse(u);
    }

    /** Convenience for controllers: resolve current principal and map. */
    public UserResponse getProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return getUserByEmail(email);
    }

    /** Example profile update; also evicts cache for that user. */
    @Transactional
    @CacheEvict(value = "users", key = "#result.email", condition = "#result != null")
    public UserResponse updateProfile(String firstName, String lastName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found: " + email));
        if (firstName != null && !firstName.isBlank()) u.setFirstName(firstName);
        if (lastName != null && !lastName.isBlank()) u.setLastName(lastName);
        return authService.toResponse(userRepository.save(u));
    }
}
