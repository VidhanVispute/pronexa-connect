package com.pronexa.connect.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pronexa.connect.entities.Contact;
import com.pronexa.connect.entities.User;
import com.pronexa.connect.forms.ContactForm;
import com.pronexa.connect.helpers.Helper;
import com.pronexa.connect.helpers.Message;
import com.pronexa.connect.helpers.MessageType;
import com.pronexa.connect.services.ContactService;
import com.pronexa.connect.services.ImageService;
import com.pronexa.connect.services.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    private final Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactService contactService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @RequestMapping("/add")
    public String addContact(Model model) {
        ContactForm contactForm = new ContactForm();
        // ===== Sending backed data to form =====
        // contactForm.setAddress("hie gurukrupa");
        // contactForm.setFavorite(true);
        model.addAttribute("contactForm", contactForm);

        return "user/add_contact";
    }

    @PostMapping("/add")
    public String saveContact(
            @Valid
            @ModelAttribute ContactForm contactForm,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes //  We useRedirectAttributes insted of Session to show message as flash
    ) {

        // 1️⃣ Validate form input
        if (result.hasErrors()) {
            // Log all validation errors
            result.getAllErrors().forEach(error -> logger.warn("Validation error: {}", error));

            // Add flash attribute
            redirectAttributes.addFlashAttribute("message",
                    Message.builder()
                            .content("Please correct the highlighted errors.")
                            .type(MessageType.red)
                            .build());

            // Redirect back to form
            return "user/add_contact";
        }

        // 2️⃣ Get the logged-in user
        String email = Helper.getLoggedInUserEmail(authentication);
        User user = userService.getUserByEmail(email).orElse(null);

        if (user == null) {
            redirectAttributes.addFlashAttribute("message",
                    Message.builder()
                            .content("User not found. Please login again.")
                            .type(MessageType.red)
                            .build());
            return "redirect:/login";
        }

        // 3️⃣ Convert ContactForm → Contact
        Contact contact = new Contact();
        contact.setName(contactForm.getName());
        contact.setFavorite(contactForm.isFavorite());
        contact.setEmail(contactForm.getEmail());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setLinkedInLink(contactForm.getLinkedInLink());
        contact.setWebsiteLink(contactForm.getWebsiteLink());
        contact.setUser(user);

        // 4️⃣ Handle contact picture upload (if provided)
        if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
            String uniqueFileName = UUID.randomUUID().toString();
            String imageUrl = imageService.uploadImage(contactForm.getContactImage(), uniqueFileName);// 2️⃣ Upload the image to Cloudinary using our ImageService
            contact.setPicture(imageUrl);
            contact.setCloudinaryImagePublicId(uniqueFileName); // 4️⃣ Save the unique public ID (filename) for later use (e.g., update or delete)
        }

        //5️⃣ Save contact in DB
        contactService.save(contact);

        // 6️⃣ Set success message
        redirectAttributes.addFlashAttribute("message",
                Message.builder()
                        .content("You have successfully added a new contact!")
                        .type(MessageType.green)
                        .build());

        logger.info("New contact added: {}", contact.getName());

        return "redirect:/user/contacts/add";
    }

    @GetMapping
    public String listContacts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "both") String searchType,
            @RequestParam(required = false) Boolean favorite,
            Model model,
            Authentication authentication
    ) {
        String email = Helper.getLoggedInUserEmail(authentication);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        Page<Contact> contactsPage = contactService.searchContacts(user.getUserId(), keyword, searchType, favorite, pageable);

        model.addAttribute("contacts", contactsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contactsPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("favorite", favorite);

        return "user/contacts";
    }

    @PostMapping("/delete/{id}")
    public String deleteContact(@PathVariable String id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        String email = Helper.getLoggedInUserEmail(authentication);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            Contact contact = contactService.getById(id)
                    .orElseThrow(() -> new RuntimeException("Contact not found"));

            // Check ownership
            if (!contact.getUser().getUserId().equals(user.getUserId())) {
                redirectAttributes.addFlashAttribute("error", "You are not authorized to delete this contact.");
                return "redirect:/user/contacts";
            }

            contactService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Contact deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting contact: " + e.getMessage());
        }

        return "redirect:/user/contacts";
    }

    @GetMapping("/view/{id}")
    public String viewContact(@PathVariable String id,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        // 1️⃣ Get logged-in user
        String email = Helper.getLoggedInUserEmail(authentication);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2️⃣ Fetch contact
        Contact contact = contactService.getById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        // 3️⃣ Ownership check
        if (!contact.getUser().getUserId().equals(user.getUserId())) {
            redirectAttributes.addFlashAttribute("error", "You are not authorized to view this contact.");
            return "redirect:/user/contacts";
        }

        // 4️⃣ Add contact to model
        model.addAttribute("contact", contact);
        return "user/contact-view"; // Thymeleaf template
    }

// Show edit form
    @GetMapping("/edit/{id}")
    public String editContactForm(@PathVariable String id,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        String email = Helper.getLoggedInUserEmail(authentication);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Contact contact = contactService.getById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        // Ownership check
        if (!contact.getUser().getUserId().equals(user.getUserId())) {
            redirectAttributes.addFlashAttribute("error", "You are not authorized to edit this contact.");
            return "redirect:/user/contacts";
        }

        model.addAttribute("contact", contact);
        return "user/contact-edit"; // Thymeleaf form template
    }

// Handle edit submission
    @PostMapping("/edit/{id}")
    public String updateContact(@PathVariable String id,
            @Valid @ModelAttribute("contact") Contact contact,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        String email = Helper.getLoggedInUserEmail(authentication);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Contact existingContact = contactService.getById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        // Ownership check
        if (!existingContact.getUser().getUserId().equals(user.getUserId())) {
            redirectAttributes.addFlashAttribute("error", "You are not authorized to edit this contact.");
            return "redirect:/user/contacts";
        }

        // Validation errors
        if (result.hasErrors()) {
            return "user/contact-edit";
        }

        // Preserve immutable fields (like ID, user)
        contact.setId(existingContact.getId());
        contact.setUser(existingContact.getUser());

        contactService.update(contact);
        redirectAttributes.addFlashAttribute("success", "Contact updated successfully.");
        return "redirect:/user/contacts/view/" + id;
    }

}
