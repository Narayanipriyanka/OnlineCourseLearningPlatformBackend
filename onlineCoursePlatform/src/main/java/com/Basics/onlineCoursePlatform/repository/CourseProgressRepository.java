package com.Basics.onlineCoursePlatform.repository;

import com.Basics.onlineCoursePlatform.entity.Course;
import com.Basics.onlineCoursePlatform.entity.CourseProgress;
import com.Basics.onlineCoursePlatform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseProgressRepository extends JpaRepository<CourseProgress, Long> {
    Optional<CourseProgress> findByUserAndCourse(User user, Course course);

}
