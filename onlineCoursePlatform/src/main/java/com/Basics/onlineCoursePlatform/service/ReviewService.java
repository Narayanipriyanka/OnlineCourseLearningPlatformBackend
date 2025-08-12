package com.Basics.onlineCoursePlatform.service;

import com.Basics.onlineCoursePlatform.DTO.ReviewDTO;
import com.Basics.onlineCoursePlatform.entity.Course;
import com.Basics.onlineCoursePlatform.entity.Review;
import com.Basics.onlineCoursePlatform.entity.User;
import com.Basics.onlineCoursePlatform.exception.NotFoundException;
import com.Basics.onlineCoursePlatform.repository.CourseRepository;
import com.Basics.onlineCoursePlatform.repository.ReviewRepository;
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
public class ReviewService {
    @Autowired
private ReviewRepository reviewRepository;
       @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public ResponseEntity<String> addRatingAndReview(Long id, Integer rating, String review, Authentication authentication) throws BadRequestException {
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).orElseThrow();
        Course course = courseRepository.findById(id).orElseThrow(() -> new NotFoundException("Course not found"));
        Optional<Review> existingReview = reviewRepository.findByCourseAndUser(course, user);
        if (existingReview.isPresent()) {
            throw new BadRequestException("You have already reviewed this course");
        }
        Review courseReview = new Review();
        courseReview.setCourse(course);
        courseReview.setUser(user);
        courseReview.setRating(rating.doubleValue());
        courseReview.setReview(review);
        reviewRepository.save(courseReview);
        return ResponseEntity.ok("Rating and review added successfully");
    }

    public ResponseEntity<List<ReviewDTO>> getCourseRatings(Long id) {
        List<Review> reviews = reviewRepository.findByCourseId(id);
        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(review -> {
                    ReviewDTO reviewDTO = new ReviewDTO();
                    reviewDTO.setId(review.getId());
                    reviewDTO.setCourseId(review.getCourse().getId());
                    reviewDTO.setUserId(review.getUser().getId());
                    reviewDTO.setUserName(review.getUser().getName());
                    reviewDTO.setRating(review.getRating());
                    reviewDTO.setReview(review.getReview());
                    return reviewDTO;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(reviewDTOs);
    }

    public Double getAverageRating(Long courseId) {
        List<Review> reviews = reviewRepository.findByCourseId(courseId);
        if (reviews.isEmpty()) {
            return null;
        }
        double sum = reviews.stream().mapToDouble(Review::getRating).sum();
        return sum / reviews.size();
    }

    public List<Review> getReviewList(Long courseId) {
        return reviewRepository.findByCourseId(courseId);

    }

}
