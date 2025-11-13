package com.hms.user.dto;

public class JwtResponse {
    private String token;
    private String tokenType = "Bearer";
    private long expiresInMs;
    private UserResponse user;

    public JwtResponse() { }

    public JwtResponse(String token, long expiresInMs, UserResponse user) {
        this.token = token;
        this.expiresInMs = expiresInMs;
        this.user = user;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; } // always "Bearer"

    public long getExpiresInMs() { return expiresInMs; }
    public void setExpiresInMs(long expiresInMs) { this.expiresInMs = expiresInMs; }

    public UserResponse getUser() { return user; }
    public void setUser(UserResponse user) { this.user = user; }
}
