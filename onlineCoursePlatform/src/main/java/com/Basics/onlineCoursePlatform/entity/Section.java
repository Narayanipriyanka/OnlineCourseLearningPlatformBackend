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
                ", videoUrl='" + videoFileName + '\'' +
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
