package com.Basics.onlineCoursePlatform.controller;

import com.Basics.onlineCoursePlatform.DTO.CourseProgressDTO;
import com.Basics.onlineCoursePlatform.service.CourseProgressService;
import com.Basics.onlineCoursePlatform.service.SectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.security.Principal;
@Controller
@RequestMapping("/api")
@SecurityRequirement(name = "bearerAuth")
public class CourseProgressController {
    @Autowired
    private CourseProgressService courseProgressService;
    @Autowired
    private SectionService sectionService;

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
    @Operation(summary = "stream video")
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("{courseId}/{sectionId}/video")
    public ResponseEntity<InputStreamResource> streamVideo(@PathVariable Long courseId, @PathVariable Long sectionId,Authentication authentication, HttpServletResponse response) throws IOException {
        return sectionService.streamVideo(courseId, sectionId, authentication);

    }








}
