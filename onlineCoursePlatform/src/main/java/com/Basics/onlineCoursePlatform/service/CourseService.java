package com.Basics.onlineCoursePlatform.service;

import com.Basics.onlineCoursePlatform.DTO.CourseDTO;
import com.Basics.onlineCoursePlatform.DTO.ReviewDTO;
import com.Basics.onlineCoursePlatform.entity.*;
import com.Basics.onlineCoursePlatform.exception.ForbiddenException;
import com.Basics.onlineCoursePlatform.exception.NotFoundException;
import com.Basics.onlineCoursePlatform.repository.CourseRepository;
import com.Basics.onlineCoursePlatform.repository.ReviewRepository;
import com.Basics.onlineCoursePlatform.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ModelMapper modelMapper;


    public ResponseEntity<Page<CourseDTO>> getCourses(int page, int size, String sortBy, String direction, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new ForbiddenException("Access denied"));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(direction.toUpperCase()), sortBy));

        Page<Course> courses = switch (user.getRole()) {
            case STUDENT -> courseRepository.findByIsPublishedTrue(pageable);
            case INSTRUCTOR -> courseRepository.findByInstructor(user, pageable);
            case ADMIN -> courseRepository.findAll(pageable);
            default -> throw new ForbiddenException("Access denied");
        };
        Page<CourseDTO> courseDTOs = courses.map(course -> {
            CourseDTO courseDTO = modelMapper.map(course, CourseDTO.class);
            List<Review> reviews = reviewRepository.findByCourse(course);
            if (!reviews.isEmpty()) {
                Double averageRating = reviews.stream().mapToDouble(Review::getRating).average().orElse(0.0);
                courseDTO.setRatings(averageRating);
            } else {
                courseDTO.setRatings(0.0);
            }
            List<ReviewDTO> reviewDTOs = reviews.stream()
                    .map(review -> {
                        ReviewDTO reviewDTO = new ReviewDTO();
                        reviewDTO.setId(review.getId());
                        reviewDTO.setCourseId(review.getCourse().getId());
                        reviewDTO.setUserId(review.getUser().getId());
                        reviewDTO.setUserName(review.getUser().getName());
                        reviewDTO.setRating(review.getRating().doubleValue());
                        reviewDTO.setReview(review.getReview());
                        return reviewDTO;
                    })
                    .collect(Collectors.toList());
            courseDTO.setReviews(reviewDTOs);
            return courseDTO;
        });


        return ResponseEntity.ok(courseDTOs);
    }



    // Add a new course
    public ResponseEntity<CourseDTO> addCourse(CourseDTO courseDTO, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new ForbiddenException("Access denied"));

        if (!user.getRole().equals(Role.INSTRUCTOR)) {
            throw new ForbiddenException("Only instructors can add courses");
        }

        Course course = modelMapper.map(courseDTO, Course.class);
        course.setInstructor(user);
        course.setIsPublished(false);
        Course savedCourse = courseRepository.save(course);

        return ResponseEntity.ok(modelMapper.map(savedCourse, CourseDTO.class));
    }






    // Update an existing course
    public ResponseEntity<Course> updateCourse(Long id, CourseDTO courseDTO, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new ForbiddenException("Access denied"));

        Course course = courseRepository.findById(id).orElseThrow(() -> new NotFoundException("Course not found"));

        if (!course.getInstructor().getId().equals(user.getId()) || !user.getRole().equals(Role.INSTRUCTOR)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to update this course");
        }

        modelMapper.map(courseDTO, course);
        Course updatedCourse = courseRepository.save(course);

        return ResponseEntity.ok(updatedCourse);
    }




    // Delete a course
    public ResponseEntity<String> deleteCourse(Long id, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).orElseThrow();


        Course course = courseRepository.findById(id).orElseThrow(() -> new NotFoundException("Course not found"));

        if (!course.getInstructor().getId().equals(user.getId()) || !user.getRole().equals(Role.INSTRUCTOR)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to delete this course");
        }

        courseRepository.delete(course);
        return ResponseEntity.ok("Course deleted successfully");
    }
    public ResponseEntity<Course> publishCourse(Long id, boolean publish, Authentication authentication) throws BadRequestException {
        Course course = courseRepository.findById(id).orElseThrow(() -> new NotFoundException("Course not found"));

        if (course.getIsPublished() == publish) {
            throw new BadRequestException("Course is already " + (publish ? "published" : "unpublished"));
        }

        course.setIsPublished(publish);
        Course updatedCourse = courseRepository.save(course);

        return ResponseEntity.ok(updatedCourse);
    }



    // Get instructor's own courses
    public ResponseEntity<List<CourseDTO>> getMyCourses(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new ForbiddenException("Access denied"));

        List<Course> courses = courseRepository.findByInstructor(user);
        if (courses.isEmpty()) {
            throw new NotFoundException("No courses found for the instructor");
        }

        return ResponseEntity.ok(courses.stream().map(course -> modelMapper.map(course, CourseDTO.class)).collect(Collectors.toList()));
    }

    // Publish or unpublish a course

    // Get published courses
    public ResponseEntity<List<CourseDTO>> getPublishedCourses() {
        List<Course> courses = courseRepository.findByIsPublishedTrue();

        if (courses.isEmpty()) {
            throw new NotFoundException("No published courses found");
        }

        return ResponseEntity.ok(courses.stream().map(course -> modelMapper.map(course, CourseDTO.class)).collect(Collectors.toList()));
    }


    // Filter courses

    public ResponseEntity<Page<CourseDTO>> filterCourses(String category, Double price, String level, int page, int size, String sortBy, String direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(direction.toUpperCase()), sortBy));

        Specification<Course> spec = null;
        if (category != null) {
            spec = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("category"), category);
        } else if (price != null) {
            spec = (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("price"), price);
        } else if (level != null) {
            try {
                Level levelEnum = Level.valueOf(level.toUpperCase());
                spec = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("level"), levelEnum);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid level");
            }
        }

        Page<Course> courses = courseRepository.findAll(spec, pageable);

        return ResponseEntity.ok(courses.map(course -> modelMapper.map(course, CourseDTO.class)));
    }

    // Get course by id
    public ResponseEntity<CourseDTO> getCourseById(Long id, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).orElseThrow();

        Course course = courseRepository.findById(id).orElseThrow(() -> new NotFoundException("Course not found"));

        if (user.getRole().equals(Role.STUDENT) && !course.getIsPublished()) {
            throw new NotFoundException("Course not found");
        }

        return ResponseEntity.ok(modelMapper.map(course, CourseDTO.class));
    }


    // Search courses
    public ResponseEntity<List<CourseDTO>> searchCourses(String query) throws BadRequestException {
        if (query == null || query.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search query cannot be empty");
        }

        String trimmedQuery = query.trim();
        List<Course> courses = courseRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(trimmedQuery, trimmedQuery);

        if (courses.isEmpty()) {
            throw new NotFoundException("No courses found matching the search query");
        }

        return ResponseEntity.ok(courses.stream().map(course -> modelMapper.map(course, CourseDTO.class)).collect(Collectors.toList()));
    }

}