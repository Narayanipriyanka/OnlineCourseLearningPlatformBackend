package com.Basics.onlineCoursePlatform.service;

import com.Basics.onlineCoursePlatform.DTO.SectionDTO;
import com.Basics.onlineCoursePlatform.entity.*;
import com.Basics.onlineCoursePlatform.exception.ForbiddenException;
import com.Basics.onlineCoursePlatform.exception.NotFoundException;
import com.Basics.onlineCoursePlatform.repository.*;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SectionService {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;
@Autowired
private SectionProgressRepository sectionProgressRepository;
    @Autowired
    private FileStorageService fileStorageService;
@Autowired
private CourseProgressRepository courseProgressRepository;
    @Autowired
    private ModelMapper modelMapper;

    public ResponseEntity<List<SectionDTO>> getSections(Long courseId, Principal principal) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Course not found"));
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new ForbiddenException("Access denied"));

        if (user.getRole().equals(Role.STUDENT) && !course.getIsPublished()) {
            throw new NotFoundException("Course not found");
        }

        if (user.getRole().equals(Role.INSTRUCTOR) && !course.getInstructor().getId().equals(user.getId())) {
            throw new ForbiddenException("Access denied");
        }
        List<Section> sections = sectionRepository.findByCourseId(courseId);
        List<SectionDTO> sectionDTOs = sections.stream()
                .map(section -> {
                    SectionDTO sectionDTO = new SectionDTO();
                    sectionDTO.setId(section.getId());
                    sectionDTO.setSectionName(section.getSectionName());
                    sectionDTO.setDescription(section.getDescription());
                    sectionDTO.setVideoFileName(section.getVideoFileName());
                    return sectionDTO;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(sectionDTOs);


    }
    public ResponseEntity<SectionDTO> addSection(Long courseId, String sectionName, String description, MultipartFile videoFile, Principal principal) throws IOException {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new ForbiddenException("Access denied"));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Course not found"));
        if (!course.getInstructor().getId().equals(user.getId())) {
            throw new ForbiddenException("Access denied");
        }
        if (videoFile.isEmpty()) {
            throw new BadRequestException("Video file is required");
        }
        Section section = new Section();
        section.setCourse(course);
        section.setSectionName(sectionName);
        section.setDescription(description);
        String uploadDir =System.getProperty("user.dir")+"uploads/videos/";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String videoFileName = videoFile.getOriginalFilename();
        videoFile.transferTo(new File(uploadDir + videoFileName));
        section.setVideoFileName(videoFileName);
        Section savedSection = sectionRepository.save(section);
        SectionDTO sectionDTO = modelMapper.map(savedSection, SectionDTO.class);
        return ResponseEntity.ok(sectionDTO);
    }
    public ResponseEntity<String> updateSectionProgress(Long courseId, Long sectionId, Double progress, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();
        Section section = sectionRepository.findById(sectionId).orElseThrow();
        CourseProgress courseProgress = courseProgressRepository.findByUserAndCourse(user, course).orElseGet(() -> {
            CourseProgress newCourseProgress = new CourseProgress();
            newCourseProgress.setUser(user);
            newCourseProgress.setCourse(course);
            newCourseProgress.setCompletedSections(new ArrayList<>());
            newCourseProgress.setProgressPercentage(0.0);
            return courseProgressRepository.save(newCourseProgress);
        });
        SectionProgress sectionProgress = sectionProgressRepository.findByCourseProgressAndSection(courseProgress, section).orElseGet(() -> {
            SectionProgress newSectionProgress = new SectionProgress();
            newSectionProgress.setCourseProgress(courseProgress);
            newSectionProgress.setSection(section);
            newSectionProgress.setProgressPercentage(0.0);
            return sectionProgressRepository.save(newSectionProgress);
        });
        sectionProgress.setProgressPercentage(progress);
        sectionProgressRepository.save(sectionProgress);
        double overallProgress = calculateOverallProgress(courseProgress);
        courseProgress.setProgressPercentage(overallProgress);
        courseProgressRepository.save(courseProgress);
        return ResponseEntity.ok("Progress updated successfully");
    }



    private double calculateOverallProgress(CourseProgress courseProgress) {
        List<SectionProgress> sectionProgresses = sectionProgressRepository.findByCourseProgress(courseProgress);
        double totalProgress = 0;
        for (SectionProgress sectionProgress : sectionProgresses) {
            totalProgress += sectionProgress.getProgressPercentage();
        }
        return totalProgress / sectionProgresses.size();
    }








    public ResponseEntity<SectionDTO> updateSection(Long courseId, Long sectionId, SectionDTO sectionDTO, MultipartFile videoFile, Principal principal) throws IOException {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new ForbiddenException("Access denied"));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Course not found"));

        if (!course.getInstructor().getId().equals(user.getId())) {
            throw new ForbiddenException("Access denied");
        }

        Section section = sectionRepository.findById(sectionId).orElseThrow(() -> new NotFoundException("Section not found"));

        if (!section.getCourse().getId().equals(courseId)) {
            throw new BadRequestException("Section does not belong to the course");
        }

        modelMapper.map(sectionDTO, section);

        if (videoFile != null && !videoFile.isEmpty()) {
            String videoFileName = fileStorageService.uploadFile(videoFile);
            section.setVideoFileName(videoFileName);
        }

        Section updatedSection = sectionRepository.save(section);

        return ResponseEntity.ok(modelMapper.map(updatedSection, SectionDTO.class));
    }

    public ResponseEntity<String> deleteSection(Long courseId, Long sectionId, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new ForbiddenException("Access denied"));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Course not found"));

        if (!course.getInstructor().getId().equals(user.getId())) {
            throw new ForbiddenException("Access denied");
        }

        Section section = sectionRepository.findById(sectionId).orElseThrow(() -> new NotFoundException("Section not found"));

        sectionRepository.delete(section);
        return ResponseEntity.ok("Section deleted successfully");
    }


    public ResponseEntity<SectionDTO> getSection(Long courseId, Long sectionId, Authentication authentication) throws BadRequestException {
        Section section = sectionRepository.findById(sectionId).orElseThrow(() -> new NotFoundException("Section not found"));
        if (!section.getCourse().getId().equals(courseId)) {
            throw new BadRequestException("Section does not belong to the course");
        }
        updateCourseProgress(courseId, sectionId, authentication);
        SectionDTO sectionDTO = modelMapper.map(section, SectionDTO.class);
        return ResponseEntity.ok(sectionDTO);
    }

    private void updateCourseProgress(Long courseId, Long sectionId, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Course not found"));
        Section section = sectionRepository.findById(sectionId).orElseThrow(() -> new NotFoundException("Section not found"));
        CourseProgress courseProgress = courseProgressRepository.findByUserAndCourse(user, course).orElseGet(() -> {
            CourseProgress newCourseProgress = new CourseProgress();
            newCourseProgress.setUser(user);
            newCourseProgress.setCourse(course);
            newCourseProgress.setCompletedSections(new ArrayList<>());
            newCourseProgress.setProgressPercentage(0.0);
            return courseProgressRepository.save(newCourseProgress);
        });
        if (!courseProgress.getCompletedSections().contains(section)) {
            courseProgress.getCompletedSections().add(section);
            double progressPercentage = calculateProgressPercentage(course, courseProgress.getCompletedSections());
            courseProgress.setProgressPercentage(progressPercentage);
            courseProgressRepository.save(courseProgress);
        }
    }

    private double calculateProgressPercentage(Course course, List<Section> completedSections) {
        List<Section> allSections = sectionRepository.findByCourse(course);
        return ((double) completedSections.size() / allSections.size()) * 100;
    }
    public ResponseEntity<InputStreamResource> streamVideo(Long courseId, Long sectionId, Authentication authentication) throws IOException {
        SectionDTO sectionDTO = getSection(courseId, sectionId, authentication).getBody();
        String uploadDir = System.getProperty("user.dir") + "uploads/videos/";
        File videoFile = new File(uploadDir + sectionDTO.getVideoFileName());
        if (!videoFile.exists()) {
            return ResponseEntity.notFound().build();
        }
        InputStreamResource resource = new InputStreamResource(new FileInputStream(videoFile));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("video/mp4"))
                .body(resource);
    }











    // Helper methods...
}
