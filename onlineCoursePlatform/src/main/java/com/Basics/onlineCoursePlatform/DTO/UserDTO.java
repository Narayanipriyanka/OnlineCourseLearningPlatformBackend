package com.Basics.onlineCoursePlatform.DTO;

import com.Basics.onlineCoursePlatform.entity.Role;

public class UserDTO {
    private Long id;
    private String name;
    private String email;
private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserDTO(String name, String email, String bio, String avatar) {
        this.name = name;
        this.email = email;

        this.bio = bio;
        this.avatar=avatar;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private String bio;
    private String avatar;
    private Role role;


}
