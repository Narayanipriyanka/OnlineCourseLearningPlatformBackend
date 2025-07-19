package com.Basics.onlineCoursePlatform.DTO;

import org.springframework.web.multipart.MultipartFile;

public class InstructorDTO {
   private Long id;
   private String name;

    public InstructorDTO(String name, String email, String password,String bio,String avatar) {
    this.name=name;
    this.email=email;
    this.password=password;
    this.bio=bio;
    this.avatar=avatar;

    }
    public InstructorDTO() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private String email;
    private String password;
    private String avatar;



    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    private String bio;

    public String getPassword() {
        return password;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }


}
