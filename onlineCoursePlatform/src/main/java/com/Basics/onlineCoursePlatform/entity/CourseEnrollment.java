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

    @ManyToOne
    private Course course;

    private LocalDateTime enrolledAt;
}
