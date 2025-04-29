package com.lms.cdac.services;

import java.util.List;
import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.CourseAssignment;
import com.lms.cdac.entities.User;

public interface CourseAssignmentService {
    CourseAssignment assignCourseToUser(User user, Course course);
    List<CourseAssignment> getAssignmentsForUser(User user);
    void deleteAssignment(Long assignmentId);
    List<CourseAssignment> getAssignmentsByResourceCenter(String resourceCenter);
    List<Course> getCoursesByUser(User user);
    List<User> getUsersByCourse(Course course);
    
    // Add method to get assignments for a student
    List<CourseAssignment> getAssignmentsForStudent(User student);
    
    // Add method to get assignments for a faculty
    List<CourseAssignment> getAssignmentsForFaculty(User faculty);
    
    // Add method to get courses assigned to a faculty
    List<Course> getCoursesByFaculty(User faculty);
    
    // Add method to get students enrolled in a course
    List<User> getStudentsByCourse(Course course);
}
