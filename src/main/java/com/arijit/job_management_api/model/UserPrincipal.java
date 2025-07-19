package com.arijit.job_management_api.model;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * SPRING SECURITY USER ADAPTER CLASS PURPOSE: Converts our User entity into
 * Spring Security's UserDetails format implements UserDetails - Interface
 * required by Spring Security for authentication ROLE: Adapter pattern -
 * bridges our User model with Spring Security requirements CREATED BY:
 * UserDetailsServiceImpl.loadUserByUsername() USED BY: Spring Security for
 * authentication and authorization
 */
public class UserPrincipal implements UserDetails {

    /**
     * USER ENTITY STORAGE Holds our actual User entity from the database
     * Contains: id, username, hashed password
     */
    private User user;

    /**
     * CONSTRUCTOR
     *
     * @param user - Our User entity from database Wraps the User entity to make
     * it compatible with Spring Security
     */
    public UserPrincipal(User user) {
        this.user = user;
    }

    /**
     * USER ROLES/PERMISSIONS DEFINITION
     *
     * @Override - Implements UserDetails.getAuthorities() CALLED BY: Spring
     * Security for authorization checks RETURNS: Collection of user's
     * roles/permissions SimpleGrantedAuthority("USER") - Assigns "USER" role to
     * all authenticated users AUTHORIZATION: All authenticated users have same
     * "USER" role (simple security model)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("USER"));
    }

    /**
     * PASSWORD RETRIEVAL FOR AUTHENTICATION
     *
     * @Override - Implements UserDetails.getPassword() CALLED BY: Spring
     * Security's DaoAuthenticationProvider for password verification RETURNS:
     * BCrypt hashed password from database CRITICAL: This hashed password is
     * compared with the plain text password from login SECURITY: Never returns
     * plain text password
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * USERNAME RETRIEVAL FOR AUTHENTICATION
     *
     * @Override - Implements UserDetails.getUsername() CALLED BY: Spring
     * Security for user identification RETURNS: Username from database (used
     * for login)
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * ACCOUNT EXPIRATION CHECK
     *
     * @Override - Implements UserDetails.isAccountNonExpired() CALLED BY:
     * Spring Security before authentication RETURNS: true = account is not
     * expired (always allowed in our app) SECURITY: If false, Spring Security
     * would reject authentication
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * ACCOUNT LOCK STATUS CHECK
     *
     * @Override - Implements UserDetails.isAccountNonLocked() CALLED BY: Spring
     * Security before authentication RETURNS: true = account is not locked
     * (always allowed in our app) SECURITY: If false, Spring Security would
     * reject authentication
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * CREDENTIALS EXPIRATION CHECK
     *
     * @Override - Implements UserDetails.isCredentialsNonExpired() CALLED BY:
     * Spring Security before authentication RETURNS: true = password is not
     * expired (always allowed in our app) SECURITY: If false, Spring Security
     * would reject authentication
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * ACCOUNT ENABLED STATUS CHECK
     *
     * @Override - Implements UserDetails.isEnabled() CALLED BY: Spring Security
     * before authentication RETURNS: true = account is enabled (always allowed
     * in our app) SECURITY: If false, Spring Security would reject
     * authentication CRITICAL: All four status methods (above) must return true
     * for successful authentication
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

}
