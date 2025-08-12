package com.Basics.onlineCoursePlatform.controller;

import com.Basics.onlineCoursePlatform.DTO.ReviewDTO;
import com.Basics.onlineCoursePlatform.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Controller
@RequestMapping("/api/courseReview")
@SecurityRequirement(name = "bearerAuth")
public class CourseReviewController {
    @Autowired
    private ReviewService reviewService;


     @Operation(summary = "Add a rating and review")
     @PostMapping("/{id}/rating")
     @PreAuthorize("hasRole('STUDENT')")
     public ResponseEntity<String> addRatingAndReview(@PathVariable Long id, @RequestParam Integer rating, @RequestParam String review, Authentication authentication) throws BadRequestException {
    return reviewService.addRatingAndReview(id, rating, review, authentication);
     }

    @Operation(summary = "get ratings & reviews of a course")
    @GetMapping("/{id}/ratings")
    public ResponseEntity<List<ReviewDTO>> getCourseRatings(@PathVariable Long id) {
        return reviewService.getCourseRatings(id);
    }

}
