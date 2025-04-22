package com.lms.cdac.repsitories;



import com.lms.cdac.entities.CourseProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CourseProgressRepository extends JpaRepository<CourseProgress, Long> {
    Optional<CourseProgress> findByStudentIdAndCourseIdAndResourceId(String studentId, Integer courseId, Integer resourceId);
    List<CourseProgress> findByStudentId(String studentId);
    
    List<CourseProgress> findAll(); // For admin
}
 