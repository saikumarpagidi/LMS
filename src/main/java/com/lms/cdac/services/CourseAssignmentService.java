package com.lms.cdac.services;

import java.util.List;
import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.CourseAssignment;
import com.lms.cdac.entities.User;

public interface CourseAssignmentService {
    CourseAssignment assignCourseToStudent(User student, Course course);
    List<CourseAssignment> getAssignmentsForStudent(User student);

    // New methods:
    void deleteAssignment(Long assignmentId);
    List<CourseAssignment> getAssignmentsByResourceCenter(String resourceCenter);
    
    // Faculty assignment methods
    CourseAssignment assignCourseToFaculty(User faculty, Course course);
    List<CourseAssignment> getAssignmentsForFaculty(User faculty);
}
