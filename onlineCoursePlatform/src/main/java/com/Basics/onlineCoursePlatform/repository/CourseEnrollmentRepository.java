package com.Basics.onlineCoursePlatform.repository;
import com.Basics.onlineCoursePlatform.entity.Course;
import com.Basics.onlineCoursePlatform.entity.CourseEnrollment;
import com.Basics.onlineCoursePlatform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {
    Optional<CourseEnrollment> findByUserAndCourse(User user, Course course);
    List<CourseEnrollment> findByUser(User user);

}

