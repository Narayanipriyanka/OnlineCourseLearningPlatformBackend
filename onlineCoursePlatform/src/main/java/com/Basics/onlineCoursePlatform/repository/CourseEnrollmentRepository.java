package com.Basics.onlineCoursePlatform.repository;
import com.Basics.onlineCoursePlatform.entity.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {
}

