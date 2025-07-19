package com.Basics.onlineCoursePlatform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;

import com.Basics.onlineCoursePlatform.entity.Course;
import com.Basics.onlineCoursePlatform.entity.Section;
import com.Basics.onlineCoursePlatform.entity.User;
import com.Basics.onlineCoursePlatform.model.Role;
import com.Basics.onlineCoursePlatform.repository.CourseRepository;
import com.Basics.onlineCoursePlatform.repository.SectionRepository;
import com.Basics.onlineCoursePlatform.repository.UserRepository;
import com.Basics.onlineCoursePlatform.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/sections")
public class SectionController {
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileStorageService fileStorageService;
    @GetMapping
    public ResponseEntity<List<Section>> getSections(@PathVariable Long courseId, Principal principal) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();

        if (user.getRole().equals(Role.STUDENT) && !course.getIsPublished()) {
            return ResponseEntity.notFound().build();
        }

        if (user.getRole().equals(Role.INSTRUCTOR) && course.getInstructor().getId().equals(user.getId())) {
            return ResponseEntity.ok(sectionRepository.findByCourse(course));
        }

        return ResponseEntity.ok(sectionRepository.findByCourse(course));
    }
    @PostMapping
    public ResponseEntity<Section> addSection(@PathVariable Long courseId,
                                              @RequestParam("section") String sectionJson,
                                              @RequestParam("videoFile") MultipartFile videoFile,
                                              Principal principal) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Section section = mapper.readValue(sectionJson, Section.class);

        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();
        if (!course.getInstructor().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }
        section.setCourse(course);

        // upload video file
        String videoFileName = fileStorageService.uploadFile(videoFile);
        section.setVideoFileName(videoFileName);

        Section savedSection = sectionRepository.save(section);
        return ResponseEntity.ok(savedSection);
    }
    @PutMapping("/{sectionId}")
    public ResponseEntity<Section> updateSection(@PathVariable Long courseId,
                                                 @PathVariable Long sectionId,
                                                 @RequestParam("section") String sectionJson,
                                                 @RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
                                                 Principal principal) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Section sectionDetails = mapper.readValue(sectionJson, Section.class);

        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        if (!user.getRole().equals(Role.INSTRUCTOR)) {
            return ResponseEntity.status(403).build();
        }
        Course course = courseRepository.findById(courseId).orElseThrow();
        if (!course.getInstructor().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }
        Section section = sectionRepository.findById(sectionId).orElseThrow();
        section.setSectionName(sectionDetails.getSectionName());
        section.setDescription(sectionDetails.getDescription());
        if (videoFile != null) {
            // upload new video file
            String videoFileName = fileStorageService.uploadFile(videoFile);
            section.setVideoFileName(videoFileName);
        }
        Section updatedSection = sectionRepository.save(section);
        return ResponseEntity.ok(updatedSection);
    }








    @DeleteMapping("/{sectionId}")
    public ResponseEntity<Void> deleteSection(@PathVariable Long courseId, @PathVariable Long sectionId, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        if (!user.getRole().equals(Role.INSTRUCTOR)) {
            return ResponseEntity.status(403).build();
        }

        Course course = courseRepository.findById(courseId).orElseThrow();
        if (!course.getInstructor().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        Section section = sectionRepository.findById(sectionId).orElseThrow();
        sectionRepository.delete(section);
        return ResponseEntity.noContent().build();
    }






}
