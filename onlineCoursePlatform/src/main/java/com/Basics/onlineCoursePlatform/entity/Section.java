package com.Basics.onlineCoursePlatform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;


@Entity

public class Section {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String sectionName;
    private String description;
    private String videoFileName;
    private Boolean isCompleted = false;

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean completed) {
        isCompleted = completed;
    }

    public Long getId() {
        return id;
    }

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }
    private Double videoDuration;

    public Double getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(Double videoDuration) {
        this.videoDuration = videoDuration;
    }


    public String getVideoFileName() {
        return videoFileName;
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", course=" + course +
                ", sectionName='" + sectionName + '\'' +
                ", description='" + description + '\'' +
                ", videoFileName='" + videoFileName + '\'' +
                '}';
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setVideoFileName(String videoFileName) {
        this.videoFileName = videoFileName;
    }


}
