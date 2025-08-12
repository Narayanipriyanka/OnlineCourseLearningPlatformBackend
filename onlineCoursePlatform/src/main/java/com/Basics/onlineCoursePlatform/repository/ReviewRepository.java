package com.Basics.onlineCoursePlatform.repository;

import com.Basics.onlineCoursePlatform.entity.Course;
import com.Basics.onlineCoursePlatform.entity.Review;
import com.Basics.onlineCoursePlatform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository  extends JpaRepository<Review, Long> {
    List<Review> findByCourseId(Long courseId);
    List<Review> findByCourse(Course course);
    Optional<Review> findByCourseAndUser(Course course, User user);

}
