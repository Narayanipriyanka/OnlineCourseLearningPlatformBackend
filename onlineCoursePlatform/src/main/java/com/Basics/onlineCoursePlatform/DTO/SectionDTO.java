package com.Basics.onlineCoursePlatform.DTO;

public class SectionDTO {
    private Long id;
    private String sectionName;
    private String description;
private String videoFileName;
    public Long getId() {
        return id;
    }

    public String getVideoFileName() {
        return videoFileName;
    }

    public void setVideoFileName(String videoFileName) {
        this.videoFileName = videoFileName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }
}
