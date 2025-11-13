package com.hms.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Thin controller to start OAuth2 login with Google.
 * Spring Security handles the rest via oauth2Login() and your OAuth2SuccessHandler.
 */
@Controller
@RequestMapping("/api/v1/oauth")
public class OAuthController {

    /**
     * Redirects to Spring Security's OAuth2 entry point for Google.
     * Your SecurityConfig must have oauth2Login() enabled and a Google client registered.
     */
    @GetMapping("/google")
    public RedirectView redirectToGoogle() {
        RedirectView rv = new RedirectView("/oauth2/authorization/google");
        rv.setStatusCode(HttpStatus.FOUND);
        return rv;
    }
}
