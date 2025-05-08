package com.example.news;

public class Comment {
    private String username;
    private String userEmail;
    private String comment;
    private String profileImageUrl;  // URL de la photo de profil
    private long timestamp;

    // Constructeur vide pour Firestore
    public Comment() { }

    public Comment(String username, String userEmail, String comment, String profileImageUrl, long timestamp) {
        this.username = username;
        this.userEmail = userEmail;
        this.comment = comment;
        this.profileImageUrl = profileImageUrl;
        this.timestamp = timestamp;
    }

    // Getters et Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
