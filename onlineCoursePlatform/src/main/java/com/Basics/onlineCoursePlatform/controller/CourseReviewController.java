package com.Basics.onlineCoursePlatform.controller;

import com.Basics.onlineCoursePlatform.DTO.CourseProgressDTO;
import com.Basics.onlineCoursePlatform.DTO.ReviewDTO;
import com.Basics.onlineCoursePlatform.entity.Course;
import com.Basics.onlineCoursePlatform.entity.Section;
import com.Basics.onlineCoursePlatform.entity.User;
import com.Basics.onlineCoursePlatform.service.CourseProgressService;
import com.Basics.onlineCoursePlatform.service.ReviewService;
import com.Basics.onlineCoursePlatform.service.SectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
@Controller
@RequestMapping("/api/courseReview")
@SecurityRequirement(name = "bearerAuth")
public class CourseReviewController {
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private CourseProgressService courseProgressService;
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
    @Operation(summary = "Get course progress")
    @GetMapping("/{id}/progress")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<CourseProgressDTO> getCourseProgress(@PathVariable Long id, Authentication authentication) {
        return courseProgressService.getCourseProgress(id, authentication);
    }
    @Operation(summary = "mark a course as complete")
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("{courseId}/{sectionId}/complete")
    public ResponseEntity<String> markSectionAsCompleted(@PathVariable Long courseId, @PathVariable Long sectionId, Principal principal) {
        courseProgressService.markSectionAsCompleted(courseId, sectionId, principal.getName());
        return ResponseEntity.ok("Section marked as completed");
    }
    @Operation(summary = "update section progress")
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/courses/{courseId}/sections/{sectionId}/progress")
    public ResponseEntity<String> updateSectionProgress(@PathVariable Long courseId, @PathVariable Long sectionId, @RequestParam Double progress, Principal principal) {
        return sectionService.updateSectionProgress(courseId, sectionId, progress, principal);

    }







}
