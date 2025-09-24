package com.pronexa.connect.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pronexa.connect.entities.Contact;

public interface ContactService {

    Contact save(Contact contact);
    Contact update(Contact contact);
    List<Contact> getAll();
    Page<Contact> getByUserId(String userId, Pageable pageable);
    Page<Contact> getFavorites(String userId, Pageable pageable);

    // MAIN STANDARD: single search method
    Page<Contact> searchContacts(String userId, String keyword, String searchType, Boolean favorite, Pageable pageable);
    void delete(String id);
    Optional<Contact> getById(String id);
}

