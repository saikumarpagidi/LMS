package com.lms.cdac.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lms.cdac.entities.Course;
import com.lms.cdac.repsitories.CourseRepository;
import com.lms.cdac.services.CourseService;

@Service  
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public List<Course> getAllCourses() { // ✅ Fixed spelling mistake (getAllCources → getAllCourses)
        return courseRepository.findAll();
    }

    @Override
    public String getCourseNameById(Integer courseId) { 
        return courseRepository.findById(courseId)
                .map(Course::getCourseName)  // ✅ Extracting course name safely
                .orElse("Unknown Course");
    }
    @Override
    public Course getCourseById(Integer courseId) {
        return courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
    }

    @Override
    public Optional<Course> findById(Integer courseId) {
        return courseRepository.findById(courseId);
    }
}
