package com.lms.cdac.services.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.CourseAssignment;
import com.lms.cdac.entities.User;
import com.lms.cdac.repositories.CourseAssignmentRepository;
import com.lms.cdac.services.CourseAssignmentService;
import com.lms.cdac.helper.ResourceNotFoundException;

@Service
public class CourseAssignmentServiceImpl implements CourseAssignmentService {

    private final CourseAssignmentRepository repository;

    public CourseAssignmentServiceImpl(CourseAssignmentRepository repository) {
        this.repository = repository;
    }

    @Override
    public CourseAssignment assignCourseToUser(User user, Course course) {
        // Check if the course is already assigned to the user
        List<CourseAssignment> existingAssignments = repository.findByUser(user);
        boolean isAlreadyAssigned = existingAssignments.stream()
                .anyMatch(assignment -> assignment.getCourse().getId().equals(course.getId()));
        
        if (isAlreadyAssigned) {
            throw new RuntimeException("This course is already assigned to the user: " + user.getName());
        }

        CourseAssignment assignment = CourseAssignment.builder()
                .user(user)
                .course(course)
                .build();
        return repository.save(assignment);
    }

    @Override
    public List<CourseAssignment> getAssignmentsForUser(User user) {
        return repository.findByUser(user);
    }
    
    @Override
    public void deleteAssignment(Long assignmentId) {
        repository.deleteById(assignmentId);
    }
    
    @Override
    public List<CourseAssignment> getAssignmentsByResourceCenter(String resourceCenter) {
        // Get all assignments
        List<CourseAssignment> assignments = repository.findAll();
        
        // Filter assignments by matching user's resource center
        return assignments.stream()
            .filter(a -> resourceCenter.equalsIgnoreCase(a.getUser().getResourceCenter()))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Course> getCoursesByUser(User user) {
        List<CourseAssignment> assignments = repository.findByUser(user);
        return assignments.stream()
                .map(CourseAssignment::getCourse)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getUsersByCourse(Course course) {
        List<CourseAssignment> assignments = repository.findByCourse(course);
        return assignments.stream()
                .map(CourseAssignment::getUser)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseAssignment> getAssignmentsForStudent(User student) {
        return repository.findByUser(student);
    }

    @Override
    public List<CourseAssignment> getAssignmentsForFaculty(User faculty) {
        return repository.findByUser(faculty);
    }
    
    @Override
    public List<Course> getCoursesByFaculty(User faculty) {
        List<CourseAssignment> assignments = repository.findByUser(faculty);
        return assignments.stream()
                .map(CourseAssignment::getCourse)
                .distinct()
                .collect(Collectors.toList());
    }
    
    @Override
    public List<User> getStudentsByCourse(Course course) {
        List<CourseAssignment> assignments = repository.findByCourse(course);
        return assignments.stream()
                .map(CourseAssignment::getUser)
                .filter(user -> user != null && user.hasRole("STUDENT"))
                .distinct()
                .collect(Collectors.toList());
    }
}
