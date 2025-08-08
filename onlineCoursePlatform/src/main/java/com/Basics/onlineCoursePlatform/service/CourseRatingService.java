package com.Basics.onlineCoursePlatform.service;

import com.Basics.onlineCoursePlatform.DTO.CourseRatingDTO;
import com.Basics.onlineCoursePlatform.entity.Course;
import com.Basics.onlineCoursePlatform.entity.CourseEnrollment;
import com.Basics.onlineCoursePlatform.entity.CourseRating;
import com.Basics.onlineCoursePlatform.entity.User;
import com.Basics.onlineCoursePlatform.exception.ForbiddenException;
import com.Basics.onlineCoursePlatform.exception.NotFoundException;
import com.Basics.onlineCoursePlatform.repository.CourseEnrollmentRepository;
import com.Basics.onlineCoursePlatform.repository.CourseRatingRepository;
import com.Basics.onlineCoursePlatform.repository.CourseRepository;
import com.Basics.onlineCoursePlatform.repository.UserRepository;

import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseRatingService {
    @Autowired
    private CourseRatingRepository courseRatingRepository;

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseEnrollmentRepository courseEnrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public ResponseEntity<String> addRatingAndReview(Long courseId, Integer rating, String review, Authentication authentication) throws BadRequestException {
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Course not found"));

        // Check if user is enrolled in the course
        List<CourseEnrollment> enrollments = courseEnrollmentRepository.findByUserAndCourseId(user, courseId);
        if (enrollments.isEmpty()) {
            throw new ForbiddenException("You are not enrolled in this course");
        }

        // Check if user has already rated the course
        Optional<CourseRating> existingRating = courseRatingRepository.findByCourseAndUser(course, user);
        if (existingRating.isPresent()) {
            throw new BadRequestException("You have already rated this course");
        }

        CourseRating courseRating = new CourseRating();
        courseRating.setCourse(course);
        courseRating.setUser(user);
        courseRating.setRating(rating);
        courseRating.setReview(review);

        courseRatingRepository.save(courseRating);

        return ResponseEntity.ok("Rating and review added successfully");
    }

    public ResponseEntity<List<CourseRatingDTO>> getCourseRatings(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Course not found"));
        List<CourseRating> ratings = courseRatingRepository.findByCourse(course);

        List<CourseRatingDTO> ratingDTOs = ratings.stream()
                .map(rating -> modelMapper.map(rating, CourseRatingDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ratingDTOs);
    }

}
