package com.pronexa.connect.services;

public interface AuthService {
    boolean verifyEmailToken(String token);
}
