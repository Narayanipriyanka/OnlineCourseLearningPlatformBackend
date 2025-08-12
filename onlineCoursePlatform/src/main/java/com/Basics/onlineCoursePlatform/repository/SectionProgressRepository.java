package com.Basics.onlineCoursePlatform.repository;

import com.Basics.onlineCoursePlatform.entity.SectionProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionProgressRepository extends JpaRepository<SectionProgress, Long> {
    List<SectionProgress> findByCourseProgressId(Long courseProgressId);

}
