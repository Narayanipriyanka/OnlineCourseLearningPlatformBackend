package com.Basics.onlineCoursePlatform.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

@Entity
public class CourseEnrollment {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private User user;

    @ManyToOne
    private Course course;

    private LocalDateTime enrolledAt;
}
