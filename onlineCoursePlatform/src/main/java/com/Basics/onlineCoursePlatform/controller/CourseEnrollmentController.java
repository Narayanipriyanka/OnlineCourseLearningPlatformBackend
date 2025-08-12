package com.Basics.onlineCoursePlatform.controller;

import com.Basics.onlineCoursePlatform.DTO.CourseDTO;
import com.Basics.onlineCoursePlatform.service.CourseEnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
@Controller
@RequestMapping("/api/course")
@SecurityRequirement(name = "bearerAuth")
public class CourseEnrollmentController {
    @Autowired
    private CourseEnrollmentService courseEnrollmentService;
    @Operation(summary = "enroll to a course if you are a student")
    @PostMapping("/{id}/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<String> enrollCourse(@PathVariable Long id, Authentication authentication) throws BadRequestException {
        return courseEnrollmentService.enrollCourse(id, authentication);
    }

    @Operation(summary = "get enrolled courses of me")
    @GetMapping("/enrolled")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<CourseDTO>> getEnrolledCourses(Authentication authentication) {
        return courseEnrollmentService.getEnrolledCourses(authentication);
    }

}
