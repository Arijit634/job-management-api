package com.arijit.job_management_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.arijit.job_management_api.model.User;
import com.arijit.job_management_api.model.UserPrincipal;
import com.arijit.job_management_api.repository.UserRepo;

/**
 * SPRING SECURITY USER LOOKUP SERVICE
 *
 * @Service - Marks this as a Spring service component for dependency injection
 * implements UserDetailsService - Interface required by Spring Security for
 * user authentication CRITICAL ROLE: Bridge between Spring Security and your
 * database Called automatically during authentication process
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * USER REPOSITORY INJECTION
     *
     * @Autowired - Spring injects UserRepo bean for database access Used to
     * query the users table in PostgreSQL
     */
    @Autowired
    private UserRepo repo;

    /**
     * CORE AUTHENTICATION METHOD
     *
     * @Override - Implements UserDetailsService.loadUserByUsername() WHEN
     * CALLED: Every time someone tries to login/authenticate CALLED BY: Spring
     * Security's DaoAuthenticationProvider automatically PURPOSE: Load user
     * data from database for authentication
     *
     * @param username - The username from the login request (from Basic Auth
     * header)
     * @return UserDetails - Spring Security format user object
     * @throws UsernameNotFoundException - If user not found (results in 401
     * error)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        /**
         * DATABASE QUERY EXECUTION repo.findByUsername(username) - JPA
         * generates: SELECT * FROM users WHERE username = ? Returns User entity
         * or null if not found
         */
        User user = repo.findByUsername(username);

        /**
         * USER EXISTENCE CHECK if (user == null) - Check if user exists in
         * database SECURITY: Prevents authentication with non-existent users
         */
        if (user == null) {
            /**
             * AUTHENTICATION FAILURE throw UsernameNotFoundException - Tells
             * Spring Security user not found RESULT: Spring Security returns
             * 401 Unauthorized to client
             */
            throw new UsernameNotFoundException("User doesn't exist");
        }

        /**
         * USER PRINCIPAL CREATION new UserPrincipal(user) - Wraps our User
         * entity in Spring Security format UserPrincipal implements UserDetails
         * interface required by Spring Security CONTAINS: username, hashed
         * password, authorities/roles, account status NEXT STEP: Spring
         * Security will use this to verify password
         */
        return new UserPrincipal(user);
    }

}
