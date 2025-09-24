package com.pronexa.connect.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.pronexa.connect.services.UserService;

public class Helper {

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(Helper.class);

    // Fetches the email of the currently logged-in user.
    // Works for both OAuth2 logins (Google, GitHub) and local authentication.
    public static String getLoggedInUserEmail(Authentication authentication) {

        // Check if the user logged in via OAuth2
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            // Get the principal which contains user attributes
            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
            // Identify which OAuth provider is used (google, github, etc.)
            String clientId = oauthToken.getAuthorizedClientRegistrationId();

            logger.info("OAuth2 login detected. Provider: {}", clientId);

            String email = null;

             // Handle Google OAuth2 login
            if (clientId.equalsIgnoreCase("google")) {
                 // Fetch email attribute directly from Google
                email = oauthUser.getAttribute("email");
                if (email == null) {
                    logger.error("Email not provided by Google OAuth2 provider.");
                } else {
                    logger.info("Fetched email from Google: {}", email);
                }
            }
             // Handle GitHub OAuth2 login 
            else if (clientId.equalsIgnoreCase("github")) {
                email = oauthUser.getAttribute("email");
                if (email == null) {
                     // GitHub may not provide email; fallback to login@github.com
                    String login = oauthUser.getAttribute("login");
                    if (login != null) {
                        email = login + "@github.com"; // safer than @gmail.com
                        logger.warn("Email not provided by GitHub; using fallback email: {}", email);
                    } else {
                        logger.error("Neither email nor login provided by GitHub OAuth2 provider.");
                    }
                } else {
                    logger.info("Fetched email from GitHub: {}", email);
                }
            } else {
                logger.warn("Unknown OAuth2 provider: {}", clientId);
            }

            return email;

        } 
         // For local authentication (username/password login)
        else {
            String username = authentication.getName();
            logger.info("Local login detected. Username: {}", username);
            return username;
        }
    }


    public static final String BASE_URL = "http://localhost:8081";

public static String getLinkForVerification(String emailToken) {
    return String.format("%s/auth/verify-email?token=%s", BASE_URL, emailToken);
}
}
