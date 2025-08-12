package com.Basics.onlineCoursePlatform.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class CourseProgress {
    @Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

    public Long getId() {
        return id;
    }

    @ManyToOne
    private User user;

    @ManyToOne
    private Course course;

    @ManyToMany
    @JoinTable(
            name = "course_progress_sections",
            joinColumns = @JoinColumn(name = "course_progress_id"),
            inverseJoinColumns = @JoinColumn(name = "section_id")
    )
    private List<Section> completedSections;


    private Double progressPercentage;




    public Course getCourse() {
        return course;
    }

    public List<Section> getCompletedSections() {
        return completedSections;
    }

    public void setCompletedSections(List<Section> completedSections) {
        this.completedSections = completedSections;
    }

    public Double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(Double progressPercentage) {
        this.progressPercentage = progressPercentage;
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
}
