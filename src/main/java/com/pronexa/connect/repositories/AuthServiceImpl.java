package com.pronexa.connect.repositories;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pronexa.connect.entities.User;
import com.pronexa.connect.services.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public boolean verifyEmailToken(String token) {
        User user = userRepo.findByEmailToken(token).orElse(null);

        if (user != null && token.equals(user.getEmailToken())) {
            user.setEmailVerified(true);
            user.setEnabled(true);
            user.setEmailToken(null); // Remove token after verification
            userRepo.save(user);
            return true;
        }
        return false;
    }
}

