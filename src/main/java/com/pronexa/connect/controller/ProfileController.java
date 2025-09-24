package com.pronexa.connect.controller;

import java.util.ArrayList;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pronexa.connect.entities.Contact;
import com.pronexa.connect.entities.User;
import com.pronexa.connect.helpers.Helper;
import com.pronexa.connect.services.UserService;

@Controller
@RequestMapping("/user/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    // Show profile page
    @GetMapping
    public String profile(Model model, Authentication authentication) {
        String email = Helper.getLoggedInUserEmail(authentication);
        User loggedInUser = userService.getUserByEmail(email).orElse(null);
        model.addAttribute("loggedInUser", loggedInUser);
        return "user/profile";
    }

    // Show edit profile form
    @GetMapping("/edit")
    public String editProfile(Model model, Authentication authentication) {
        String email = Helper.getLoggedInUserEmail(authentication);
        User loggedInUser = userService.getUserByEmail(email).orElse(null);
        model.addAttribute("loggedInUser", loggedInUser);
        return "user/edit-profile";
    }

    // Handle edit profile submission
    @PostMapping("/edit")
    public String saveProfile(@ModelAttribute("loggedInUser") User updatedUser,
                              Authentication authentication) {
        String email = Helper.getLoggedInUserEmail(authentication);
        User existingUser = userService.getUserByEmail(email).orElse(null);

        if (existingUser != null) {
            // Update basic fields
            existingUser.setName(updatedUser.getName());
            existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
            existingUser.setAbout(updatedUser.getAbout());

            if (updatedUser.getProfilePic() != null && !updatedUser.getProfilePic().isEmpty()) {
                existingUser.setProfilePic(updatedUser.getProfilePic());
            }

            // Update roleList safely
            existingUser.getRoleList().clear();
            if (updatedUser.getRoleList() != null) {
                existingUser.getRoleList().addAll(new ArrayList<>(updatedUser.getRoleList()));
            }

            // Update contacts safely if included
            if (updatedUser.getContacts() != null) {
                existingUser.getContacts().clear();
                for (Contact contact : updatedUser.getContacts()) {
                    contact.setUser(existingUser); // maintain relationship
                    existingUser.getContacts().add(contact);
                }
            }

            userService.saveUser(existingUser);
        }

        return "redirect:/user/profile";
    }

    // Show change password form
    @GetMapping("/change-password")
    public String changePassword() {
        return "user/change-password";
    }

    // Handle password update
    @PostMapping("/change-password")
    public String savePassword(@RequestParam String newPassword, Authentication authentication) {
        String email = Helper.getLoggedInUserEmail(authentication);
        User user = userService.getUserByEmail(email).orElse(null);
        if (user != null) {
            // Ideally, encode with BCrypt before saving
            user.setPassword(newPassword);
            userService.saveUser(user);
        }
        return "redirect:/user/profile";
    }
}
