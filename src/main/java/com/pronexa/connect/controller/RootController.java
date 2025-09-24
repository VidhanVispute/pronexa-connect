
package com.pronexa.connect.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.pronexa.connect.entities.User;
import com.pronexa.connect.helpers.Helper;
import com.pronexa.connect.services.UserService;

@ControllerAdvice
public class RootController {

    private static final Logger logger = LoggerFactory.getLogger(RootController.class);

    @Autowired
    private UserService userService;

    /**
     * This method adds the logged-in user to the model for all controllers automatically.
     */
    @ModelAttribute
    public void addLoggedInUserInformation(Model model, Authentication authentication) {
        if (authentication == null) {
            return; // user not logged in, skip
        }

        String email = Helper.getLoggedInUserEmail(authentication);
        logger.info("Logged in user: {}", email);

        // Fetch user from DB safely
        User user = userService.getUserByEmail(email).orElse(null);

        model.addAttribute("loggedInUser", user);
    }
}

