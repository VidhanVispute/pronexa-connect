package com.pronexa.connect.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.pronexa.connect.helpers.AppConstant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    private String userId;

    @Column(name = "user_name", nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Column(length = 1000)
    private String about;

    @Column(length = 1000)
    private String profilePic;

    private String phoneNumber;

    private boolean enabled = false;
    private boolean emailVerified = false;
    private boolean phoneNumberVerified = false;

    @Enumerated(EnumType.STRING)
    private Providers provider = Providers.SELF; // Default: SELF registration

    private String providerUserId;

    private String emailToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Contact> contacts = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roleList = new ArrayList<>();

    @Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    if (roleList == null || roleList.isEmpty()) {
        return List.of(new SimpleGrantedAuthority(AppConstant.ROLE_USER));
    }
    return roleList.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
}

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public String getPassword() {
        return this.password;
    }
}
