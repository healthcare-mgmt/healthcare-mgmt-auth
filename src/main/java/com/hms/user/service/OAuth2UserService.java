package com.hms.user.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Thin wrapper over DefaultOAuth2UserService.
 * We keep it as a bean so SecurityConfig can inject it and we can customize later if needed.
 */
@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User delegate = super.loadUser(userRequest);

        // Use "email" as the principal name when available; fallback to "sub"
        String nameAttributeKey = delegate.getAttributes().containsKey("email")
                ? "email" : "sub";

        // Default authorities here are empty; SuccessHandler handles role mapping + JWT.
        return new DefaultOAuth2User(
                Set.of(new SimpleGrantedAuthority("ROLE_PATIENT")),
                delegate.getAttributes(),
                nameAttributeKey
        );
    }
}
