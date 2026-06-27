package com.dmis.auth.model;

public record LoginResponse(String accessToken, String tokenType, long expiresIn) {
}
