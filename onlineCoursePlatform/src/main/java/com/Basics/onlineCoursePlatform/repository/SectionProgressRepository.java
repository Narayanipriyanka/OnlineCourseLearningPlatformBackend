package com.Basics.onlineCoursePlatform.repository;

import com.Basics.onlineCoursePlatform.entity.CourseProgress;
import com.Basics.onlineCoursePlatform.entity.Section;
import com.Basics.onlineCoursePlatform.entity.SectionProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SectionProgressRepository extends JpaRepository<SectionProgress, Long> {
    List<SectionProgress> findByCourseProgressId(Long courseProgressId);
    Optional<SectionProgress> findByCourseProgressAndSection(CourseProgress courseProgress, Section section);
    List<SectionProgress> findByCourseProgress(CourseProgress courseProgress);
}
