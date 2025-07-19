package com.arijit.job_management_api.service;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {

    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    @Autowired
    private JwtService jwtService;

    public void blacklistToken(String token) {
        if (token != null && !token.trim().isEmpty()) {
            blacklistedTokens.add(token);
            System.out.println("Token blacklisted successfully. Total blacklisted tokens: " + blacklistedTokens.size());
        }
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

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

    public int getBlacklistSize() {
        return blacklistedTokens.size();
    }

    public void clearBlacklist() {
        int size = blacklistedTokens.size();
        blacklistedTokens.clear();
        System.out.println("Cleared " + size + " tokens from blacklist");
    }
}
