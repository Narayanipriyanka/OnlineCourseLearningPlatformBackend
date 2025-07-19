package com.Basics.onlineCoursePlatform.controller;

import com.Basics.onlineCoursePlatform.entity.Course;
import com.Basics.onlineCoursePlatform.entity.User;
import com.Basics.onlineCoursePlatform.model.Role;
import com.Basics.onlineCoursePlatform.repository.CourseRepository;
import com.Basics.onlineCoursePlatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;

    @GetMapping("/instructors")
      public ResponseEntity<List<User>> getInstructors() {
        return ResponseEntity.ok(userRepository.findByRole(Role.INSTRUCTOR));
    }

    @GetMapping("/instructors/{id}/courses")
    public ResponseEntity<List<Course>> getInstructorCourses(@PathVariable Long id) {
        User instructor = userRepository.findById(id).orElseThrow();
        return ResponseEntity.ok(courseRepository.findByInstructor(instructor));
    }

    @GetMapping("/students")
    public ResponseEntity<List<User>> getStudents() {
        return ResponseEntity.ok(userRepository.findByRole(Role.STUDENT));
    }

    @PostMapping("/instructors")
    public ResponseEntity<User> addInstructor(@RequestBody User user) {
        user.setRole(Role.INSTRUCTOR);
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @DeleteMapping("/instructors/{id}")
    public ResponseEntity<Void> deleteInstructor(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
