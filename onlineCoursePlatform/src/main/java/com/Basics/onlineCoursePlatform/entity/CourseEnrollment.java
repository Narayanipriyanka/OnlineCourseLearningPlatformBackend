package com.Basics.onlineCoursePlatform.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class CourseEnrollment {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }



    @ManyToOne
    private Course course;

    private LocalDateTime enrolledAt;
}
