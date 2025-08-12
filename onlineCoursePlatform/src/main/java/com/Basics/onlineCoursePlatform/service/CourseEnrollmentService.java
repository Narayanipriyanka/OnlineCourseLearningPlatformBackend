package com.Basics.onlineCoursePlatform.service;

import com.Basics.onlineCoursePlatform.DTO.CourseDTO;
import com.Basics.onlineCoursePlatform.DTO.ReviewDTO;
import com.Basics.onlineCoursePlatform.entity.*;
import com.Basics.onlineCoursePlatform.exception.NotFoundException;
import com.Basics.onlineCoursePlatform.repository.CourseEnrollmentRepository;
import com.Basics.onlineCoursePlatform.repository.CourseProgressRepository;
import com.Basics.onlineCoursePlatform.repository.CourseRepository;
import com.Basics.onlineCoursePlatform.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseEnrollmentService {
    @Autowired
    private CourseEnrollmentRepository courseEnrollmentRepository;
@Autowired
private CourseProgressRepository courseProgressRepository;
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReviewService reviewService;

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
        CourseProgress courseProgress = new CourseProgress();
        courseProgress.setUser(user);
        courseProgress.setCourse(course);
        courseProgress.setCompletedSections(new ArrayList<>());
        courseProgress.setProgressPercentage(0.0);
        courseProgressRepository.save(courseProgress);

        return ResponseEntity.ok("Enrolled in course successfully");
    }


    public ResponseEntity<List<CourseDTO>> getEnrolledCourses(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).orElseThrow();
        List<CourseEnrollment> enrollments = courseEnrollmentRepository.findByUser(user);
        List<CourseDTO> courses = enrollments.stream()
                .map(enrollment -> {
                    CourseDTO courseDTO = modelMapper.map(enrollment.getCourse(), CourseDTO.class);
                    Double averageRating = reviewService.getAverageRating(enrollment.getCourse().getId());
                    List<ReviewDTO> reviewDTOs = reviewService.getReviewList(enrollment.getCourse().getId()).stream()
                            .map(review -> modelMapper.map(review, ReviewDTO.class))
                            .collect(Collectors.toList());
                    courseDTO.setRatings(averageRating);
                    courseDTO.setReviews(reviewDTOs);
                    return courseDTO;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(courses);
    }








}
