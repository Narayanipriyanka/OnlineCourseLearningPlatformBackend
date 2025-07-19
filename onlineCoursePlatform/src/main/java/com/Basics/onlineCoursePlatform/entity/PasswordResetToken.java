package com.Basics.onlineCoursePlatform.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;

    private String token;
    private LocalDateTime expiresAt;
    private Boolean isUsed;
    public User getUser() {
        return user;
    }
    public PasswordResetToken() {
    }

    public Boolean getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(Boolean used) {
        isUsed = used;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getToken() {
        return token;
    }
    public PasswordResetToken(User user, String token, LocalDateTime expiresAt, boolean isUsed) {
        this.user = user;
        this.token = token;
        this.expiresAt = expiresAt;
        this.isUsed = isUsed;
    }


    public void setToken(String token) {
        this.token = token;
    }

    public void setUser(User user) {
        this.user = user;
    }


}
