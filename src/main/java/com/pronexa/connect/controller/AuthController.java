package com.pronexa.connect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pronexa.connect.helpers.Message;
import com.pronexa.connect.helpers.MessageType;
import com.pronexa.connect.services.AuthService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token, HttpSession session) {

        boolean verified = authService.verifyEmailToken(token);

        if (verified) {
            session.setAttribute("message", Message.builder()
                    .type(MessageType.green)
                    .content("Your email is verified. You can now log in.")
                    .build());
            return "success_page";
        } else {
            session.setAttribute("message", Message.builder()
                    .type(MessageType.red)
                    .content("Email verification failed. Invalid or expired token.")
                    .build());
            return "error_page";
        }
    }
}
