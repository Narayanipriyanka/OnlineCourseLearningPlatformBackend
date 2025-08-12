package com.Basics.onlineCoursePlatform.service;

import com.Basics.onlineCoursePlatform.DTO.CourseProgressDTO;
import com.Basics.onlineCoursePlatform.entity.Course;
import com.Basics.onlineCoursePlatform.entity.CourseProgress;
import com.Basics.onlineCoursePlatform.entity.Section;
import com.Basics.onlineCoursePlatform.entity.User;
import com.Basics.onlineCoursePlatform.exception.NotFoundException;
import com.Basics.onlineCoursePlatform.repository.CourseProgressRepository;
import com.Basics.onlineCoursePlatform.repository.CourseRepository;
import com.Basics.onlineCoursePlatform.repository.SectionRepository;
import com.Basics.onlineCoursePlatform.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseProgressService {
    @Autowired
    private CourseProgressRepository courseProgressRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SectionService sectionService;


    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private ModelMapper modelMapper;
    public ResponseEntity<CourseProgressDTO> getCourseProgress(Long courseId, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Course not found"));
        CourseProgress courseProgress = courseProgressRepository.findByUserAndCourse(user, course).orElseGet(() -> {
            CourseProgress newCourseProgress = new CourseProgress();
            newCourseProgress.setUser(user);
            newCourseProgress.setCourse(course);
            newCourseProgress.setCompletedSections(new ArrayList<>());
            newCourseProgress.setProgressPercentage(0.0);
            return courseProgressRepository.save(newCourseProgress);
        });
        CourseProgressDTO courseProgressDTO = new CourseProgressDTO();
        courseProgressDTO.setId(courseProgress.getId());
        courseProgressDTO.setUserId(courseProgress.getUser().getId());
        courseProgressDTO.setCourseId(courseProgress.getCourse().getId());
        courseProgressDTO.setProgressPercentage(courseProgress.getProgressPercentage());
        return ResponseEntity.ok(courseProgressDTO);
    }











    public void markSectionAsCompleted(Long courseId, Long sectionId, String username) {
        User user = userRepository.findByEmail(username).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();
        Section section = sectionRepository.findById(sectionId).orElseThrow();
        CourseProgress courseProgress = courseProgressRepository.findByUserAndCourse(user, course).orElseThrow();

        section.setIsCompleted(true);
        sectionRepository.save(section);

        List<Section> sections = sectionRepository.findByCourseId(courseId);
        long completedSections = sections.stream().filter(Section::getIsCompleted).count();
        double progress = ((double) completedSections / sections.size()) * 100;
        courseProgress.setProgressPercentage(progress);
        courseProgressRepository.save(courseProgress);
    }

}
