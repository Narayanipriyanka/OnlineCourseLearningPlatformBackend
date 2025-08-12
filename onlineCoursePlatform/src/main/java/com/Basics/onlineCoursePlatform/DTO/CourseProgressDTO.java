package com.Basics.onlineCoursePlatform.DTO;

public class CourseProgressDTO {
    private Long id;
    private Long userId;
    private Long courseId;
    private Double progressPercentage;

    public Long getUserId() {
        return userId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public Double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(Double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
