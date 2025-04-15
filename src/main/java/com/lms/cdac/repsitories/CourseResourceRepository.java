package com.lms.cdac.repsitories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.lms.cdac.entities.CourseResource;
import com.lms.cdac.entities.CourseTopic;
import com.lms.cdac.entities.Course;

@Repository
public interface CourseResourceRepository extends JpaRepository<CourseResource, Integer> {

    // ✅ Correct method to get resources by course ID
    @Query("SELECT cr FROM CourseResource cr WHERE cr.course.id = :courseId")
    List<CourseResource> findByCourseId(@Param("courseId") Integer courseId);

    // ✅ Correct method to get resources by topic ID
    @Query("SELECT cr FROM CourseResource cr WHERE cr.courseTopic.id = :topicId")
    List<CourseResource> findByCourseTopicId(@Param("topicId") Integer topicId);

    // ✅ Optional method to get resources using entity reference
    List<CourseResource> findByCourse(Course course);

    List<CourseResource> findByCourseTopic(CourseTopic courseTopic);
}
