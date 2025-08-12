package com.Basics.onlineCoursePlatform.DTO;

public class ReviewDTO {
    private Long id;
    private Long courseId;
    private Long userId;
    private String userName;
    private Double rating;
    private String review;

    public Long getCourseId() {
        return courseId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Double getRating() {
        return rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
