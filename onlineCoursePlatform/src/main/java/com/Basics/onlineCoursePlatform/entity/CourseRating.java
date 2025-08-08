package com.Basics.onlineCoursePlatform.entity;

import jakarta.persistence.*;

@Entity
public class CourseRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Course course;

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne
    private User user;

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    private Integer rating;

    private String review;
}
