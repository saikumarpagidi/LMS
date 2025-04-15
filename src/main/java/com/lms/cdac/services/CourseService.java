package com.lms.cdac.services;

import java.util.List;
import java.util.Optional;
import com.lms.cdac.entities.Course;

public interface CourseService {
    Course saveCourse(Course course);
    List<Course> getAllCourses();
    String getCourseNameById(Integer courseId); 
    Course getCourseById(Integer courseId);
    Optional<Course> findById(Integer courseId);
}
