package com.pronexa.connect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pronexa.connect.services.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String userDashboard() {
        // 'loggedInUser' is automatically available in Thymeleaf
        return "user/dashboard";
    }

    // @GetMapping("/profile")
    // public String profile() {
    //     // 'loggedInUser' is already in the model
    //     return "user/profile";
    // }


    // Add other user pages later (add/edit/view contacts)
}
