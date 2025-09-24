package com.pronexa.connect.config;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.pronexa.connect.entities.Providers;
import com.pronexa.connect.entities.User;
import com.pronexa.connect.helpers.AppConstant;
import com.pronexa.connect.repositories.UserRepo;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class OAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuthAuthenticationSuccessHandler.class);

    @Autowired
    private UserRepo userRepo;

    // === STEP : 1 ===> 
    // This code only handles the redirection after a successful OAuth login. 
    // It does not save or update user information in the database because:
    // This method is called when OAuth login is successful
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        logger.info("OAuthAuthenticationSuccessHandler invoked");

        // Cast Authentication object to OAuth2AuthenticationToken to access provider info
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String providerId = oauthToken.getAuthorizedClientRegistrationId();
        DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();

        logger.info("OAuth Provider: {}", providerId);
        oauthUser.getAttributes().forEach((key, value) -> logger.info("{} : {}", key, value));

        // Map OAuth2 user attributes to our application's User entity
        User user = buildUserFromOAuth(providerId, oauthUser);

        // Save or update user with fallback for profilePic
        User existingUser = userRepo.findByEmail(user.getEmail()).orElse(null);

        if (existingUser == null) {
            userRepo.save(user);
            logger.info("New user saved: {}", user.getEmail());
        } else {
            // Always update existing user's profilePic if null/empty
            if (existingUser.getProfilePic() == null || existingUser.getProfilePic().isEmpty()) {
                existingUser.setProfilePic("/css/default-user.jpeg");
                userRepo.save(existingUser);
                logger.info("Updated default profile pic for: {}", existingUser.getEmail());
            }
        }

        // ✅ Set session for frontend
User dbUser = existingUser != null ? existingUser : user;
HttpSession session = request.getSession();
session.setAttribute("loggedInUser", dbUser);

        // Redirect to user profile
        new DefaultRedirectStrategy().sendRedirect(request, response, "/user/profile");
    }

    // === STEP : 2 ===>
    // Helper method: Convert OAuth2 attributes to User entity means save data in DB
    private User buildUserFromOAuth(String providerId, DefaultOAuth2User oauthUser) {
        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setRoleList(List.of(AppConstant.ROLE_USER));
        user.setEmailVerified(true);
        user.setEnabled(true);
        user.setPassword("dummy"); // Since OAuth handles authentication - users usually don't need a password

        switch (providerId.toLowerCase()) {
            case "google" -> {
                user.setEmail(oauthUser.getAttribute("email"));
                user.setName(oauthUser.getAttribute("name"));

                String picture = oauthUser.getAttribute("picture");
                if (picture == null || picture.trim().isEmpty()) {
                    picture = "/css/default-user.jpeg"; // fallback default
                }
                user.setProfilePic(picture);

                user.setProviderUserId(oauthUser.getName());
                user.setProvider(Providers.GOOGLE);
                user.setAbout("This account is created using Google.");
            }

            case "github" -> {
                String email = oauthUser.getAttribute("email") != null
                        ? oauthUser.getAttribute("email")
                        : oauthUser.getAttribute("login") + "@github.com";
                user.setEmail(email);
                user.setName(oauthUser.getAttribute("login"));
                String githubPic = oauthUser.getAttribute("avatar_url");
                if (githubPic == null || githubPic.isEmpty()) {
                    githubPic = "/css/default-user.jpeg";
                }
                user.setProfilePic(githubPic);
                user.setProviderUserId(oauthUser.getName());
                user.setProvider(Providers.GITHUB);
                user.setAbout("This account is created using GitHub.");
            }

            case "linkedin" -> // Add LinkedIn attribute mapping here
                logger.info("LinkedIn OAuth mapping not implemented yet.");

            default ->
                logger.warn("Unknown OAuth provider: {}", providerId);
        }

        return user;
    }
}
