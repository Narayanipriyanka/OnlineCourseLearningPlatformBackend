package com.Basics.onlineCoursePlatform.controller;


import com.Basics.onlineCoursePlatform.DTO.CourseDTO;
import com.Basics.onlineCoursePlatform.DTO.InstructorDTO;
import com.Basics.onlineCoursePlatform.DTO.StudentDTO;

import com.Basics.onlineCoursePlatform.entity.User;
import com.Basics.onlineCoursePlatform.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Operation(summary = "Get list of instructors")
    @GetMapping("/instructors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InstructorDTO>> getInstructors(Authentication authentication) {
        return ResponseEntity.ok(adminService.getInstructors(authentication));
    }

    @Operation(summary = "Get instructor courses")
    @GetMapping("/instructors/{id}/courses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CourseDTO>> getInstructorCourses(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(adminService.getInstructorCourses(id, authentication));
    }

    @Operation(summary = "Get students")
    @GetMapping("/students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StudentDTO>> getStudents(Authentication authentication) {
        return ResponseEntity.ok(adminService.getStudents(authentication));
    }

    @Operation(summary = "Add instructor Add a new instructor")
    @PostMapping(value = "/instructors", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InstructorDTO> addInstructor(@RequestPart("avatar") MultipartFile avatar,
                                              @RequestParam("name") String name,
                                              @RequestParam("email") String email,
                                              @RequestParam("password") String password,
                                              @RequestParam("bio") String bio
    ) throws IOException {
       return adminService.addInstructor(avatar,name,email,password,bio);
    }






    @Operation(summary = "Delete instructor Delete an instructor")
    @DeleteMapping("/instructors/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteInstructor(@PathVariable Long id, Authentication authentication) {
        adminService.deleteInstructor(id, authentication);
        return ResponseEntity.ok("Instructor deleted successfully");
    }


}

