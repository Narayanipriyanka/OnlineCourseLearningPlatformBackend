package com.Basics.onlineCoursePlatform.repository;

import com.Basics.onlineCoursePlatform.entity.Course;
import com.Basics.onlineCoursePlatform.entity.CourseRating;
import com.Basics.onlineCoursePlatform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRatingRepository extends JpaRepository<CourseRating, Long> {
    List<CourseRating> findByCourse(Course course);
    Optional<CourseRating> findByCourseAndUser(Course course, User user);

}
