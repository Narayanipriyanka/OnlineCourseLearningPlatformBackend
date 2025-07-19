package com.Basics.onlineCoursePlatform.controller;

import com.Basics.onlineCoursePlatform.entity.Course;
import com.Basics.onlineCoursePlatform.entity.Level;
import com.Basics.onlineCoursePlatform.entity.Section;
import com.Basics.onlineCoursePlatform.entity.User;
import com.Basics.onlineCoursePlatform.model.Role;
import com.Basics.onlineCoursePlatform.repository.CourseRepository;
import com.Basics.onlineCoursePlatform.repository.SectionRepository;
import com.Basics.onlineCoursePlatform.repository.UserRepository;
import com.Basics.onlineCoursePlatform.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    @Autowired
    private CourseRepository courseRepository;
@Autowired
private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SectionRepository sectionRepository;

    @GetMapping
    public ResponseEntity<List<Course>> getCourses(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).orElseThrow();
        if (user.getRole().equals(Role.STUDENT)) {
            return ResponseEntity.ok(courseRepository.findByIsPublishedTrue());
        } else if (user.getRole().equals(Role.INSTRUCTOR)) {
            return ResponseEntity.ok(courseRepository.findByInstructor(user));
        } else {
            return ResponseEntity.ok(courseRepository.findAll());
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<Course> addCourse(@RequestBody Course course, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).orElseThrow();
        course.setInstructor(user);
        course.setIsPublished(false);
        Course savedCourse = courseRepository.save(course);
        return ResponseEntity.ok(savedCourse);
    }

    // Add a new course






    private List<GrantedAuthority> getAuthorities(Role role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_"+role.name()));
        return authorities;
    }





    // Update an existing course
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @RequestBody Course courseDetails, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Course course = courseRepository.findById(id).orElseThrow();
        if (!course.getInstructor().getId().equals(user.getId()) || !user.getRole().equals(Role.INSTRUCTOR)) {
            return ResponseEntity.status(403).build();
        }
        course.setTitle(courseDetails.getTitle());
        course.setDescription(courseDetails.getDescription());
        course.setCategory(courseDetails.getCategory());
        course.setPrice(courseDetails.getPrice());
        course.setLevel(courseDetails.getLevel());
        Course updatedCourse = courseRepository.save(course);
        return ResponseEntity.ok(updatedCourse);
    }


    // Delete a course
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Course course = courseRepository.findById(id).orElseThrow();
        if (!course.getInstructor().getId().equals(user.getId()) || !user.getRole().equals(Role.INSTRUCTOR)) {
            return ResponseEntity.status(403).build();
        }
        courseRepository.delete(course);
        return ResponseEntity.noContent().build();
    }


    // Publish or unpublish a course



    // Get instructor's own courses
    @GetMapping("/my-courses")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<List<Course>> getMyCourses(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).orElseThrow();
        List<Course> courses = courseRepository.findByInstructor(user);
        return ResponseEntity.ok(courses);
    }



    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isCourseOwner(authentication.name, #id)")
    public ResponseEntity<Course> publishCourse(@PathVariable Long id, @RequestParam boolean publish, Authentication authentication) {
        Course course = courseRepository.findById(id).orElseThrow();
        course.setIsPublished(publish);
        Course updatedCourse = courseRepository.save(course);
        return ResponseEntity.ok(updatedCourse);
    }


    // List all published courses
    @GetMapping("/published")
    public ResponseEntity<List<Course>> getPublishedCourses() {
        List<Course> courses = courseRepository.findByIsPublishedTrue();
        return ResponseEntity.ok(courses);
    }

    // Filter courses by category, price, level
    @GetMapping("/filter")
    public ResponseEntity<List<Course>> filterCourses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) String level) {
        List<Course> courses = courseRepository.findAll();
        if (category != null) {
            courses.retainAll(courseRepository.findByCategory(category));
        }
        if (price != null) {
            courses.retainAll(courseRepository.findByPriceLessThanEqual(price));
        }
        if (level != null) {
            try {
                Level levelEnum = Level.valueOf(level.toUpperCase());
                courses.retainAll(courseRepository.findByLevel(levelEnum));
            } catch (IllegalArgumentException e) {
                // Handle invalid level
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.ok(courses);
    }


    // View single course details
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).orElseThrow();
        Course course = courseRepository.findById(id).orElseThrow();
        if (user.getRole().equals(Role.STUDENT) && !course.getIsPublished()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(course);
    }



    // Search functionality (title, description)

    @GetMapping("/search")
    public ResponseEntity<List<Course>> searchCourses(@RequestParam String query) {
        String trimmedQuery = query.trim();
        List<Course> courses = courseRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(trimmedQuery, trimmedQuery);
        return ResponseEntity.ok(courses);
    }







}

