package com.lms.cdac.repsitories;

import com.lms.cdac.entities.CourseModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseModuleRepository extends JpaRepository<CourseModule, Integer> {
    List<CourseModule> findByCourseId(Integer courseId);
    
    @Query("SELECT c.course.id FROM CourseModule c WHERE c.id = :moduleId")
    Optional<Integer> findCourseIdByModuleId(@Param("moduleId") Integer moduleId);
}
