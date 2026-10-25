package com.fyp.gridguard.Models;

import com.fyp.gridguard.Utilities.Roles;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

//    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Roles roles;

    private Users users;

    public UserPrincipal(Users user) {
        this.users = user;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        System.out.println(users.getRoles().name());  // ADMIN
        String completeUser = "ROLE_" + users.getRoles().name().toUpperCase();
//        System.out.println(">>> CompleteUser: "+ completeUser);
        return List.of(new SimpleGrantedAuthority(completeUser));
    }

    @Override
    public String getPassword() {
        return users.getPassword();
    }

    @Override
    public String getUsername() {
        return users.getEmail();
    }

    public String getUserEmail() {
        return users.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

//    @Override
//    public boolean isEnabled() {
//        System.out.println("isEnabled: " + users.isActive());
//        return users.isActive();
//    }
}
