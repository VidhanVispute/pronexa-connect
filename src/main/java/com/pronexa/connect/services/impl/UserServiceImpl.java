package com.pronexa.connect.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pronexa.connect.entities.User;
import com.pronexa.connect.helpers.AppConstant;
import com.pronexa.connect.helpers.Helper;
import com.pronexa.connect.helpers.ResourceNotFoundException;
import com.pronexa.connect.repositories.UserRepo;
import com.pronexa.connect.services.EmailService;
import com.pronexa.connect.services.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepo userRepo;  // Repository layer to interact with the database

    @Autowired
    private EmailService emailService;


    private final Logger logger = LoggerFactory.getLogger(this.getClass()); // Logger for debugging and monitoring

   @Override
public User saveUser(User user) {
    logger.info("Saving user: " + user);

    // Generate user ID
    user.setUserId(UUID.randomUUID().toString());

    // Encode password
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    // Set default role
    user.setRoleList(List.of(AppConstant.ROLE_USER));

    // Default profile picture
    if (user.getProfilePic() == null || user.getProfilePic().isEmpty()) {
        user.setProfilePic("/css/default-user.jpeg");
    }

    // Generate email verification token and send email
    String emailToken = UUID.randomUUID().toString();
    user.setEmailToken(emailToken);
    

    
    // Save user
    User savedUser = userRepo.save(user);
    String emailLink = Helper.getLinkForVerification(emailToken);
    
    // Optionally save token in DB or cache
    // savedUser.setEmailVerificationToken(emailToken);
    // userRepo.save(savedUser);

    emailService.sendEmail(
            savedUser.getEmail(),
            "🔒 Verify your NexaConnect Account",
            "Hello " + savedUser.getEmail() + ",\n\nPlease verify your account by clicking the link below:\n" + emailLink + "\n\nThank you,\nNexaConnect Team"
    );

    return savedUser;
}


    @Override
    public Optional<User> getUserById(String id) {
        logger.info("Fetching user by id: " + id);
        // Retrieve user by ID; returns Optional to handle null cases gracefully
        return userRepo.findById(id);
    }

    @Override
    public Optional<User> updateUser(User user) {
        logger.info("Updating user: " + user);
        // Retrieve existing user or throw exception if not found
        User existingUser = userRepo.findById(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + user.getUserId() + " not found"));

        // Update relevant fields only (do not overwrite everything unless necessary)
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());
        existingUser.setAbout(user.getAbout());
        existingUser.setProfilePic(user.getProfilePic());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setEnabled(user.isEnabled());
        existingUser.setEmailVerified(user.isEmailVerified());
        existingUser.setPhoneNumberVerified(user.isPhoneNumberVerified());
        existingUser.setProvider(user.getProvider());
        existingUser.setProviderUserId(user.getProviderUserId());
        // Note: Contacts list is not replaced to avoid accidental data loss

        // Save the updated user entity to the database
        User updatedUser = userRepo.save(existingUser);

        // Return updated user wrapped in Optional
        return Optional.of(updatedUser);
    }

    @Override
    public void deleteUser(String id) {
        logger.info("Deleting user with id: " + id);

        // Retrieve existing user or throw exception if not found
        User existingUser = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found"));

        // Delete the retrieved user entity
        userRepo.delete(existingUser);
    }

    @Override
    public boolean isUserExists(String userId) {
        logger.info("Checking if user exists with id: " + userId);
        // Check existence by attempting to fetch user, throws exception if not found
        userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
        return true; // If no exception, user exists
    }

    @Override
    public boolean isUserExistsByEmail(String email) {
        logger.info("Checking if user exists with email: " + email);
        // Check existence by email, throws exception if not found
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found")) != null;
    }

    @Override
    public List<User> getAllUser() {
        logger.info("Fetching all users");
        // Retrieve all users from the database
        List<User> users = userRepo.findAll();
        if (users.isEmpty()) {
            logger.warn("No users found in the database");
        }
        return users; // Return list of users
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }
}
