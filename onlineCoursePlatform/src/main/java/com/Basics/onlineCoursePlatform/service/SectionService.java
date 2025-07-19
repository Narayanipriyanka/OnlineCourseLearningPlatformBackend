package com.Basics.onlineCoursePlatform.service;

import com.Basics.onlineCoursePlatform.DTO.SectionDTO;
import com.Basics.onlineCoursePlatform.entity.Course;
import com.Basics.onlineCoursePlatform.entity.Role;
import com.Basics.onlineCoursePlatform.entity.Section;
import com.Basics.onlineCoursePlatform.entity.User;
import com.Basics.onlineCoursePlatform.exception.ForbiddenException;
import com.Basics.onlineCoursePlatform.exception.NotFoundException;
import com.Basics.onlineCoursePlatform.repository.CourseRepository;
import com.Basics.onlineCoursePlatform.repository.SectionRepository;
import com.Basics.onlineCoursePlatform.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

        List<Section> sections = sectionRepository.findByCourse(course);
        List<SectionDTO> sectionDTOs = sections.stream()
                .map(section -> modelMapper.map(section, SectionDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(sectionDTOs);
    }



    public ResponseEntity<SectionDTO> addSection(Long courseId, SectionDTO sectionDTO, MultipartFile videoFile, Principal principal) throws IOException {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new ForbiddenException("Access denied"));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Course not found"));

        if (!course.getInstructor().getId().equals(user.getId())) {
            throw new ForbiddenException("Access denied");
        }

        if (videoFile.isEmpty()) {
            throw new BadRequestException("Video file is required");
        }

        Section section = modelMapper.map(sectionDTO, Section.class);
        section.setCourse(course);
        String videoFileName = fileStorageService.uploadFile(videoFile);
        section.setVideoFileName(videoFileName);
        Section savedSection = sectionRepository.save(section);

        return ResponseEntity.ok(modelMapper.map(savedSection, SectionDTO.class));
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











    // Helper methods...
}
