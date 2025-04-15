package com.lms.cdac.repsitories;

import com.lms.cdac.entities.CourseResource;
import com.lms.cdac.entities.CourseTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseTopicRepository extends JpaRepository<CourseTopic, Integer> {
    List<CourseTopic> findByCourseModule_Id(Integer moduleId); 
   // List<CourseResource> findByCourseTopic(CourseTopic courseTopic);

    // ✅ Fixed Query Method
  //  List<CourseTopic> findByCourseModule_Id(Integer moduleId);
}
