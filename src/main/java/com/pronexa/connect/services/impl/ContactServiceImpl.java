package com.pronexa.connect.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pronexa.connect.entities.Contact;
import com.pronexa.connect.helpers.ResourceNotFoundException;
import com.pronexa.connect.repositories.ContactRepo;
import com.pronexa.connect.services.ContactService;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepo contactRepo;

    @Override
    public Contact save(Contact contact) {
        contact.setId(UUID.randomUUID().toString());
        return contactRepo.save(contact);
    }

    @Override
    public Contact update(Contact contact) {
        var contactOld = contactRepo.findById(contact.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        contactOld.setName(contact.getName());
        contactOld.setEmail(contact.getEmail());
        contactOld.setPhoneNumber(contact.getPhoneNumber());
        contactOld.setAddress(contact.getAddress());
        contactOld.setDescription(contact.getDescription());
        contactOld.setPicture(contact.getPicture());
        contactOld.setFavorite(contact.isFavorite());
        contactOld.setWebsiteLink(contact.getWebsiteLink());
        contactOld.setLinkedInLink(contact.getLinkedInLink());
        contactOld.setCloudinaryImagePublicId(contact.getCloudinaryImagePublicId());

        return contactRepo.save(contactOld);
    }

    @Override
    public List<Contact> getAll() {
        return contactRepo.findAll();
    }

    @Override
    public Page<Contact> getByUserId(String userId, Pageable pageable) {
        return contactRepo.findByUserId(userId, pageable);
    }

    // Day 24: search
    @Override
    public Page<Contact> searchContacts(String userId, String keyword, String searchType, Boolean favorite, Pageable pageable) {
        if (favorite != null && favorite) {
            return getFavorites(userId, pageable);
        }
        if (keyword == null || keyword.isBlank()) {
            return getByUserId(userId, pageable);
        }

        return switch (searchType.toLowerCase()) {
            case "name" -> contactRepo.searchByName(userId, keyword, pageable);
            case "email" -> contactRepo.searchByEmail(userId, keyword, pageable);
            default -> contactRepo.searchByUserIdAndKeyword(userId, keyword, pageable);
        };
    }

    // Day 24: favorites
    @Override
    public Page<Contact> getFavorites(String userId, Pageable pageable) {
        return contactRepo.findFavoritesByUserId(userId, pageable);
    }

    @Override
    public void delete(String id) {
        Contact  contact = contactRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with given id " + id));
        contactRepo.delete(contact);
    }

    @Override
    public Optional<Contact> getById(String id) {
        return contactRepo.findById(id);
    }
}
