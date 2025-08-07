package com.Basics.onlineCoursePlatform.service;

import com.Basics.onlineCoursePlatform.DTO.CourseDTO;
import com.Basics.onlineCoursePlatform.entity.Course;
import com.Basics.onlineCoursePlatform.entity.CourseEnrollment;
import com.Basics.onlineCoursePlatform.entity.Role;
import com.Basics.onlineCoursePlatform.entity.User;
import com.Basics.onlineCoursePlatform.exception.ForbiddenException;
import com.Basics.onlineCoursePlatform.exception.NotFoundException;
import com.Basics.onlineCoursePlatform.repository.CourseEnrollmentRepository;
import com.Basics.onlineCoursePlatform.repository.CourseRepository;
import com.Basics.onlineCoursePlatform.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseEnrollmentService {
    @Autowired
    private CourseEnrollmentRepository courseEnrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;
@Autowired
private ModelMapper modelMapper;
    public ResponseEntity<String> enrollCourse(Long courseId, Authentication authentication) throws BadRequestException {
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Course not found"));
        Optional<CourseEnrollment> existingEnrollment = courseEnrollmentRepository.findByUserAndCourse(user, course);
        if (existingEnrollment.isPresent()) {
            throw new BadRequestException("You are already enrolled in this course");
        }

        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setUser(user);
        enrollment.setCourse(course);
        enrollment.setEnrolledAt(LocalDateTime.now());

        courseEnrollmentRepository.save(enrollment);

        return ResponseEntity.ok("Enrolled in course successfully");
    }

    public ResponseEntity<List<CourseDTO>> getEnrolledCourses(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).orElseThrow();

        List<CourseEnrollment> enrollments = courseEnrollmentRepository.findByUser(user);
        List<CourseDTO> courses = enrollments.stream()
                .map(enrollment -> modelMapper.map(enrollment.getCourse(), CourseDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(courses);
    }

}
