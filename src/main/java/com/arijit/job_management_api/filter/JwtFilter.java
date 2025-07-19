package com.arijit.job_management_api.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.arijit.job_management_api.service.JwtService;
import com.arijit.job_management_api.service.TokenBlacklistService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT AUTHENTICATION FILTER
 *
 * @Component - Makes this a Spring-managed bean that gets auto-injected
 * OncePerRequestFilter - Ensures this filter runs exactly once per HTTP request
 *
 * PURPOSE: Intercepts every HTTP request to check for JWT tokens in the
 * Authorization header EXECUTION ORDER: This filter runs BEFORE the actual
 * controller methods
 *
 * FLOW: 1. Extract JWT token from Authorization header 2. Validate the token
 * and extract username 3. Load user details from database 4. Set authentication
 * in Spring Security context 5. Continue to next filter/controller
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    /**
     * JWT SERVICE INJECTION
     *
     * @Autowired - Spring automatically injects our JWT service Used for: Token
     * extraction, validation, and username extraction
     */
    @Autowired
    private JwtService jwtService;

    /**
     * TOKEN BLACKLIST SERVICE INJECTION
     *
     * @Autowired - Spring automatically injects TokenBlacklistService Used for:
     * Checking if JWT tokens are blacklisted (logged out)
     */
    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    /**
     * USER DETAILS SERVICE INJECTION
     *
     * @Autowired - Spring automatically injects our UserDetailsServiceImpl Used
     * for: Loading user details from database once JWT is validated
     */
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * MAIN FILTER METHOD - RUNS ON EVERY HTTP REQUEST
     *
     * @Override - Overrides the parent class method
     *
     * PARAMETERS: - HttpServletRequest request: Incoming HTTP request -
     * HttpServletResponse response: HTTP response to be sent - FilterChain
     * filterChain: Chain of filters to execute
     *
     * FLOW EXPLANATION: 1. Check if Authorization header exists 2. Extract JWT
     * token from "Bearer <token>" format 3. Validate token and extract username
     * 4. Load user from database 5. Set authentication in Spring Security
     * context 6. Continue to next filter
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        /**
         * STEP 1: EXTRACT AUTHORIZATION HEADER authHeader - Gets the
         * "Authorization" header from HTTP request Expected format: "Bearer
         * eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." Returns null if header
         * doesn't exist
         */
        String authHeader = request.getHeader("Authorization");
        String token = null;        // Will store the actual JWT token (without "Bearer ")
        String userName = null;     // Will store username extracted from JWT token

        /**
         * STEP 2: VALIDATE HEADER FORMAT AND EXTRACT TOKEN authHeader != null -
         * Checks if Authorization header exists authHeader.startsWith("Bearer
         * ") - Validates correct JWT format token = authHeader.substring(7) -
         * Removes "Bearer " (7 characters) to get pure token userName =
         * jwtService.extractUserName(token) - Extracts username from JWT
         * payload
         */
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);  // Remove "Bearer " prefix
            System.out.println("[FILTER] Checking token for blacklist: [" + token + "]");

            /**
             * STEP 2.5: CHECK IF TOKEN IS BLACKLISTED
             *
             * Before processing the token, check if it's been blacklisted
             * (logged out) If token is blacklisted, skip authentication and
             * continue to next filter This ensures logged-out users cannot
             * access protected endpoints
             */
            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                System.out.println("Token is blacklisted, denying access");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token is blacklisted. Please login again.");
                return;  // Exit early, don't authenticate or continue filter chain
            }

            userName = jwtService.extractUserName(token);  // Extract username from token
        }

        /**
         * STEP 3: VALIDATE TOKEN AND SET AUTHENTICATION userName != null -
         * Ensures we successfully extracted username from token
         * SecurityContextHolder.getContext().getAuthentication() == null -
         * Ensures user isn't already authenticated This prevents
         * re-authentication on the same request
         */
        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            /**
             * STEP 4: LOAD USER DETAILS FROM DATABASE
             * userDetailsService.loadUserByUsername(userName) - Calls our
             * UserDetailsServiceImpl This loads full user info from database
             * (username, password, authorities) UserDetails interface provides
             * Spring Security with user information
             */
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

            /**
             * STEP 5: VALIDATE TOKEN AGAINST USER DETAILS
             * jwtService.validateToken(token, userDetails) - Validates: - Token
             * signature is valid - Token hasn't expired - Token belongs to this
             * specific user - Token hasn't been tampered with
             */
            if (jwtService.validateToken(token, userDetails)) {

                /**
                 * STEP 6: CREATE AUTHENTICATION TOKEN
                 * UsernamePasswordAuthenticationToken - Spring Security's
                 * authentication object Parameters: - userDetails: User
                 * information from database - null: No password needed (already
                 * validated via JWT) - userDetails.getAuthorities(): User's
                 * roles/permissions
                 */
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                /**
                 * STEP 7: SET AUTHENTICATION DETAILS setDetails() - Adds
                 * additional info like IP address, session ID
                 * WebAuthenticationDetailsSource().buildDetails(request) -
                 * Extracts request details
                 */
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                /**
                 * STEP 8: SET AUTHENTICATION IN SECURITY CONTEXT
                 * SecurityContextHolder.getContext().setAuthentication(authToken)
                 * - CRITICAL STEP This tells Spring Security that the user is
                 * authenticated Now user can access protected endpoints The
                 * authentication persists for the duration of this request
                 */
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        /**
         * STEP 9: CONTINUE TO NEXT FILTER filterChain.doFilter(request,
         * response) - MUST CALL THIS Passes request to next filter in chain (or
         * to controller if this is last filter) Without this, the request would
         * stop here and never reach the controller
         */
        filterChain.doFilter(request, response);
    }
}
