package com.Basics.onlineCoursePlatform.entity;

import jakarta.persistence.*;

@Entity
public class SectionProgress {
    @Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

    public Double getProgressPercentage() {
        return progressPercentage;
    }

    public Double getVideoDurationWatched() {
        return videoDurationWatched;
    }

    public void setVideoDurationWatched(Double videoDurationWatched) {
        this.videoDurationWatched = videoDurationWatched;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean completed) {
        isCompleted = completed;
    }

    public void setProgressPercentage(Double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public CourseProgress getCourseProgress() {
        return courseProgress;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public void setCourseProgress(CourseProgress courseProgress) {
        this.courseProgress = courseProgress;
    }

    @ManyToOne
    @JoinColumn(name = "course_progress_id")
    private CourseProgress courseProgress;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;

    private Double progressPercentage;
    private Boolean isCompleted;
    private Double videoDurationWatched;


}
