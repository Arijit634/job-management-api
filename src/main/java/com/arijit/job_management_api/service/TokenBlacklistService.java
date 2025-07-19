package com.arijit.job_management_api.service;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TOKEN BLACKLIST SERVICE
 *
 * Manages invalidated JWT tokens to implement logout functionality. Since JWT
 * tokens are stateless, we need to maintain a blacklist of invalidated tokens
 * until they naturally expire.
 *
 * Features: - Thread-safe token blacklisting using ConcurrentHashMap -
 * Automatic cleanup of expired blacklisted tokens - Token validation against
 * blacklist
 */
@Service
public class TokenBlacklistService {

    /**
     * THREAD-SAFE TOKEN BLACKLIST STORAGE
     *
     * ConcurrentHashMap - Thread-safe map for storing blacklisted tokens Key:
     * JWT token string Value: Expiration date of the token
     *
     * Why we store expiration date: - To clean up expired blacklisted tokens
     * automatically - To prevent memory leaks from accumulating old tokens
     */
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    @Autowired
    private JwtService jwtService;

    /**
     * BLACKLIST A JWT TOKEN
     *
     * Called when user logs out to invalidate their token.
     *
     * @param token - The JWT token to blacklist
     *
     * Process: 1. Add token to blacklist set 2. Token will be checked against
     * this blacklist on future requests 3. Token remains blacklisted until it
     * naturally expires
     */
    public void blacklistToken(String token) {
        if (token != null && !token.trim().isEmpty()) {
            blacklistedTokens.add(token);
            System.out.println("Token blacklisted successfully. Total blacklisted tokens: " + blacklistedTokens.size());
        }
    }

    /**
     * CHECK IF TOKEN IS BLACKLISTED
     *
     * Called by JwtFilter to validate tokens before allowing access.
     *
     * @param token - The JWT token to check
     * @return true if token is blacklisted (invalid), false if token is valid
     *
     * Process: 1. Check if token exists in blacklist 2. If blacklisted, token
     * is invalid 3. If not blacklisted, token may still be valid (subject to
     * other validations)
     */
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    /**
     * CLEANUP EXPIRED BLACKLISTED TOKENS
     *
     * Removes expired tokens from blacklist to prevent memory leaks. This
     * method can be called periodically or manually.
     *
     * Process: 1. Iterate through all blacklisted tokens 2. Check if each token
     * has expired 3. Remove expired tokens from blacklist 4. Keep only
     * non-expired tokens
     *
     * Note: In production, this should be scheduled to run periodically using
     *
     * @Scheduled annotation or external scheduler
     */
    public void cleanupExpiredTokens() {
        int initialSize = blacklistedTokens.size();

        blacklistedTokens.removeIf(token -> {
            try {
                // Extract expiration date from token
                Date expiration = jwtService.extractExpiration(token);
                // Remove if token has expired
                return expiration.before(new Date());
            } catch (Exception e) {
                // If token parsing fails, remove it from blacklist
                System.out.println("Invalid token found in blacklist, removing: " + e.getMessage());
                return true;
            }
        });

        int cleanedUp = initialSize - blacklistedTokens.size();
        if (cleanedUp > 0) {
            System.out.println("Cleaned up " + cleanedUp + " expired tokens from blacklist");
        }
    }

    /**
     * GET BLACKLIST STATISTICS
     *
     * Returns information about the current blacklist state. Useful for
     * monitoring and debugging.
     *
     * @return Number of currently blacklisted tokens
     */
    public int getBlacklistSize() {
        return blacklistedTokens.size();
    }

    /**
     * CLEAR ALL BLACKLISTED TOKENS
     *
     * Removes all tokens from blacklist. Use with caution - only for testing or
     * emergency situations.
     */
    public void clearBlacklist() {
        int size = blacklistedTokens.size();
        blacklistedTokens.clear();
        System.out.println("Cleared " + size + " tokens from blacklist");
    }
}
