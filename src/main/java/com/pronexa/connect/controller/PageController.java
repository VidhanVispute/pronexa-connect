package com.pronexa.connect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.pronexa.connect.entities.User;
import com.pronexa.connect.forms.UserForm;
import com.pronexa.connect.helpers.Message;
import com.pronexa.connect.helpers.MessageType;
import com.pronexa.connect.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class PageController {

    @Autowired
    private UserService userService;

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/service")
    public String service() {
        return "service";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @GetMapping("/login")
public String loginPage(HttpServletRequest request, Model model) {
    HttpSession session = request.getSession(false);
    if (session != null) {
        Message message = (Message) session.getAttribute("message");
        if (message != null) {
            model.addAttribute("message", message);
            session.removeAttribute("message"); // remove after showing
        }
    }
    return "login";
}


    @GetMapping("/signup")
    public String signup(Model model) {
        // Create a new empty form object to bind form inputs
        UserForm userForm = new UserForm();

        // Add the form object to the model so Thymeleaf can access it in the signup
        // template
        model.addAttribute("userForm", userForm);

        // Return the signup view
        return "signup";
    }

    // Handles the form submission when the user registers
    @RequestMapping(value = "/do-register", method = RequestMethod.POST)
    public String processRegister(@Valid @ModelAttribute UserForm userForm, BindingResult rBindingResult, HttpSession session) {
        // userForm now holds the data submitted from the form inputs
        // You can perform validations here if required before saving
        if (rBindingResult.hasErrors()) {
            return "signup";
        }
        // Convert the form data to the actual User entity to persist it in the database
        // User user = User.builder()
        // .name(userForm.getName()) // Set name from form
        // .about(userForm.getAbout()) // Set about section
        // .email(userForm.getEmail()) // Set email address
        // .password(userForm.getPassword()) // Set password (you should encode this
        // before saving!)
        // .phoneNumber(userForm.getPhoneNumber()) // Set phone number
        // .profilePic("https://www.learncodewithdurgesh.com/_next/image?url=%2F_next%2Fstatic%2Fmedia%2Fdurgesh_sir.35c6cb78.webp&w=1920&q=75")
        // // Default profile picture URL
        // .build();
        User user = new User();
        user.setName(userForm.getName());
        user.setAbout(userForm.getAbout());
        user.setEmail(userForm.getEmail());
        user.setPassword(userForm.getPassword());
        user.setPhoneNumber(userForm.getPhoneNumber());
        // user.setProfilePic("https://www.learncodewithdurgesh.com/_next/image?url=%2F_next%2Fstatic%2Fmedia%2Fdurgesh_sir.35c6cb78.webp&w=1920&q=75");
        // user.setProfilePic("/css/default-user.jpeg");  we done this throug serviceimpl

        // Call the service layer to save the user in the database
        User savedUser = userService.saveUser(user);
        System.out.println(savedUser + " user is saved");
        System.out.println(userForm);

        // 3️⃣ Create a Message object for feedback to the user
        Message message = new Message();
        message.setContent("Registration successful!"); // The text shown to the user
        message.setType(MessageType.green); // Type determines styling (green for success)

        // 4️⃣ Store message in session to show in the next page (Thymeleaf can access
        // session.message)
        session.setAttribute("message", message);

        // After successful registration, redirect the user to the login page
        return "redirect:/signup";
    }
}
