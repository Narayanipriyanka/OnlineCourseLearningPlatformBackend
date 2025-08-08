package com.Basics.onlineCoursePlatform.DTO;

public class CourseRatingDTO {
    private Long id;
    private Long courseId;
    private Long userId;
    private String userName;
    private Integer rating;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String review;

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
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

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
