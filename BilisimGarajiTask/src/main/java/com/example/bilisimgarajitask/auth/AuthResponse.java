package com.example.bilisimgarajitask.auth;

public record AuthResponse(
        String accessToken,
        long   accessTokenExpiresInMs,
        String refreshToken,
        long   refreshTokenExpiresInMs,
        String tokenType // "Bearer"
) {}
