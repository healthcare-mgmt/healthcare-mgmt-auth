package com.hms.user.dto;

/**
 * Optional helper DTO if you ever want to expose/transport normalized OAuth user info.
 * Not required for the login flow to work, but handy for debugging or profile sync.
 */
public class OAuthUserInfo {
    private String provider;       // e.g., "google"
    private String providerUserId; // e.g., Google sub claim
    private String email;
    private String firstName;
    private String lastName;
    private String pictureUrl;

    public OAuthUserInfo() { }

    public OAuthUserInfo(String provider, String providerUserId, String email,
                         String firstName, String lastName, String pictureUrl) {
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.pictureUrl = pictureUrl;
    }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getProviderUserId() { return providerUserId; }
    public void setProviderUserId(String providerUserId) { this.providerUserId = providerUserId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPictureUrl() { return pictureUrl; }
    public void setPictureUrl(String pictureUrl) { this.pictureUrl = pictureUrl; }
}
