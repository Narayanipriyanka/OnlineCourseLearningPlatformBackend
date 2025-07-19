package com.Basics.onlineCoursePlatform.controller;

import com.Basics.onlineCoursePlatform.DTO.CourseDTO;

import com.Basics.onlineCoursePlatform.entity.Course;
import com.Basics.onlineCoursePlatform.service.CourseService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/courses")
@SecurityRequirement(name = "bearerAuth")
public class CourseController {

  @Autowired
  private CourseService courseService;

    @Operation(summary = "get course based on pages")
    @GetMapping
    public ResponseEntity<Page<CourseDTO>> getCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @Parameter(hidden = true) Authentication authentication
    ) {
        return courseService.getCourses(page,size,sortBy,direction,authentication);
    }

    @Operation(summary = "Add a new course")
    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<CourseDTO> addCourse(@RequestBody CourseDTO courseDTO, @Parameter(hidden = true) Authentication authentication) {
       return courseService.addCourse(courseDTO,authentication);
    }




    // Update an existing course


    @Operation(summary = "Update an existing course")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR')and @courseSecurityService.isCourseOwner(authentication.name, #id)")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @RequestBody CourseDTO courseDTO, @Parameter(hidden = true) Authentication authentication) {
     return courseService.updateCourse(id,courseDTO,authentication);
    }

    // Delete a course
    @Operation(summary = "Delete a course")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR')and @courseSecurityService.isCourseOwner(authentication.name, #id)")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id, @Parameter(hidden = true) Authentication authentication) {
     return courseService.deleteCourse(id,authentication);
    }



    // Publish or unpublish a course
    // Get instructor's own courses
    @Operation(summary = "Get courses of an instructor")
    @GetMapping("/my-courses")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<List<CourseDTO>> getMyCourses(@Parameter(hidden = true) Authentication authentication) {
        return courseService.getMyCourses(authentication);
    }


    @Operation(summary = "Publish or unpublish a course")
    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isCourseOwner(authentication.name, #id)")
    public ResponseEntity<Course> publishCourse(@PathVariable Long id, @RequestParam boolean publish, @Parameter(hidden = true) Authentication authentication) throws BadRequestException {
       return courseService.publishCourse(id,publish,authentication);
    }




    // List all published courses
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get published courses", description = "Get published courses")
    @GetMapping("/published")
    public ResponseEntity<List<CourseDTO>> getPublishedCourses() {
       return courseService.getPublishedCourses();
    }




    // Filter courses by category, price, level

    @Operation(summary = "Filter courses", description = "Filter courses by category, price, level")
    @GetMapping("/filter")
    public ResponseEntity<Page<CourseDTO>> filterCourses(
            @Parameter(description = "Category") @RequestParam(required = false) String category,
            @Parameter(description = "Price") @RequestParam(required = false) Double price,
            @Parameter(description = "Level") @RequestParam(required = false) String level,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direction") @RequestParam(defaultValue = "asc") String direction) {

       return courseService.filterCourses(category,price,level,page,size,sortBy,direction);
    }




    // View single course details
    @Operation(summary = "Get course by id", description = "Get course by id")
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id, @Parameter(hidden = true) Authentication authentication) {
       return courseService.getCourseById(id,authentication);
    }

    // Search functionality (title, description)

    @Operation(summary= "Search courses", description = "Search courses by title or description")
    @GetMapping("/search")
    public ResponseEntity<List<CourseDTO>> searchCourses(@RequestParam String query) throws BadRequestException {
       return courseService.searchCourses(query);
    }









}

