package com.Basics.onlineCoursePlatform.repository;

import com.Basics.onlineCoursePlatform.entity.Course;
import com.Basics.onlineCoursePlatform.entity.Level;
import com.Basics.onlineCoursePlatform.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CourseRepository extends JpaRepository<Course,Long>, JpaSpecificationExecutor<Course> {
    Page<Course> findByInstructor(User instructor, Pageable pageable);
    List<Course> findByInstructor(User instructor);
    Page<Course> findByIsPublishedTrue(Pageable pageable);
    List<Course> findByIsPublishedTrue();
    List<Course> findByCategory(String category);
    List<Course> findByPriceLessThanEqual(Double price);
    List<Course> findByLevel(Level level);
    List<Course> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);

}
