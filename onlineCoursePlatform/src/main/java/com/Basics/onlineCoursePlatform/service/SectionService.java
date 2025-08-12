package com.Basics.onlineCoursePlatform.service;

import com.Basics.onlineCoursePlatform.DTO.SectionDTO;
import com.Basics.onlineCoursePlatform.entity.*;
import com.Basics.onlineCoursePlatform.exception.ForbiddenException;
import com.Basics.onlineCoursePlatform.exception.NotFoundException;
import com.Basics.onlineCoursePlatform.repository.CourseProgressRepository;
import com.Basics.onlineCoursePlatform.repository.CourseRepository;
import com.Basics.onlineCoursePlatform.repository.SectionRepository;
import com.Basics.onlineCoursePlatform.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
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
    private FileStorageService fileStorageService;
@Autowired
private CourseProgressRepository courseProgressRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CourseProgressService courseProgressService;

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
        updateSectionProgressLogic(user, course, section, progress);
        return ResponseEntity.ok("Progress updated successfully");
    }
    private void updateSectionProgressLogic(User user, Course course, Section section, Double progress) {
        CourseProgress courseProgress = courseProgressService.getCourseProgress(user, course);
        if (courseProgress != null) {
            List<Section> sections = sectionRepository.findByCourseId(course.getId());
            double totalProgress = 0;
            for (Section s : sections) {
                if (s.getId().equals(section.getId())) {
                    totalProgress += progress;
                } else {
                    CourseProgress existingProgress = courseProgressService.getCourseProgress(user, course);
                    List<Section> completedSections = existingProgress.getCompletedSections();
                    if (completedSections.contains(s)) {
                        totalProgress += 100;
                    } else {
                        totalProgress += 0;
                    }
                }
            }
            double overallProgress = totalProgress / sections.size();
            courseProgress.setProgressPercentage(overallProgress);
            if (progress == 100) {
                if (courseProgress.getCompletedSections() != null && !courseProgress.getCompletedSections().contains(section)) {
                    courseProgress.getCompletedSections().add(section);
                }
            }
            courseProgressRepository.save(courseProgress);
        }
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
        courseProgressService.updateCourseProgress(courseId, sectionId, authentication);
        SectionDTO sectionDTO = modelMapper.map(section, SectionDTO.class);
        return ResponseEntity.ok(sectionDTO);
    }











    // Helper methods...
}
