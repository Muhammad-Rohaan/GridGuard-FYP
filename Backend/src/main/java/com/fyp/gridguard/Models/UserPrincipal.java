package com.fyp.gridguard.Models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security adapter that wraps a {@link Users} entity and
 * exposes it as a {@link UserDetails} object.
 *
 * <p>Authorities are derived from the user's {@link com.fyp.gridguard.Utilities.Roles}
 * enum, prefixed with "ROLE_" so that Spring Security's {@code hasRole()} checks work
 * without further configuration, e.g.:
 * <pre>
 *   Roles.TECHNICAL_WORKER → authority "ROLE_TECHNICAL_WORKER"
 * </pre>
 */
public class UserPrincipal implements UserDetails {

    private final Users user;

    public UserPrincipal(Users user) {
        this.user = user;
    }

    // ----------------------------------------------------------------
    // Convenience accessor for use inside services / filters
    // ----------------------------------------------------------------
    public Users getUser() {
        return user;
    }

    // ----------------------------------------------------------------
    // UserDetails contract
    // ----------------------------------------------------------------

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // "ROLE_USER", "ROLE_TECHNICAL_WORKER", or "ROLE_ADMIN"
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRoles().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /** Username in this system is the user's email address */
    @Override
    public String getUsername() {
        return user.getEmail();
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

    /** Reflects the {@link Users#isActive()} flag for soft-disabling accounts */
    @Override
    public boolean isEnabled() {
        return user.isActive();
    }
}
