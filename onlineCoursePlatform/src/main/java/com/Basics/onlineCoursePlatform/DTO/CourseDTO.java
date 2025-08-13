package com.Basics.onlineCoursePlatform.DTO;

import com.Basics.onlineCoursePlatform.entity.Level;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.List;

public class CourseDTO {
    public void setId(long id) {
        this.id = id;
    }

    private long id;
    private String title;
    private String description;
    private String category;
    private BigDecimal price;
    private Level level;
    public long getId() {
        return id;
    }
    private Double ratings;
    private List<ReviewDTO> reviews;

    public Double getRatings() {
        return ratings;
    }

    public void setRatings(Double ratings) {
        this.ratings = ratings;
    }

    public List<ReviewDTO> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewDTO> reviews) {
        this.reviews = reviews;
    }

    public String getTitle() {
        return title;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
