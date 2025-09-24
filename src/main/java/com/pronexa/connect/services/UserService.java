package com.pronexa.connect.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pronexa.connect.entities.User;




@Service
public interface  UserService {

User saveUser(User user);

Optional <User> getUserById(String id);

Optional <User> updateUser(User user);

void deleteUser (String id);

boolean isUserExists(String userId);

boolean isUserExistsByEmail(String email);

List<User> getAllUser();

Optional<User> getUserByEmail(String email);


// Add more method relate to user 


    
}
