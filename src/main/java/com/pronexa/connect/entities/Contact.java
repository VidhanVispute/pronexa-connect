package com.pronexa.connect.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contact {

    @Id
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String picture;

    @Column(length = 1000)
    private String description;
    private boolean favorite = false;
    private String websiteLink;
    private String linkedInLink;
    private String cloudinaryImagePublicId;

    @ManyToOne  // Many contacts can belong to one user
    private User user;  // Reference to the user who owns this contact

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<SocialLink> links = new ArrayList<>();

    // Defines a one-to-many relationship where one contact can have multiple social links.
// 'mappedBy = "contact"' specifies that the 'contact' field in SocialLink owns the relationship.
// 'cascade = CascadeType.ALL' propagates all operations (persist, merge, remove, etc.) from Contact to SocialLink.
// 'fetch = FetchType.EAGER' loads the social links immediately when the contact is loaded.
// 'orphanRemoval = true' automatically deletes social links if they are removed from the list.
}
