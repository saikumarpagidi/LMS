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

@Service
public class CourseAssignmentServiceImpl implements CourseAssignmentService {

    private final CourseAssignmentRepository repository;

    public CourseAssignmentServiceImpl(CourseAssignmentRepository repository) {
        this.repository = repository;
    }

    @Override
    public CourseAssignment assignCourseToStudent(User student, Course course) {
        CourseAssignment assignment = CourseAssignment.builder()
                .student(student)
                .course(course)
                .build();
        return repository.save(assignment);
    }

    @Override
    public List<CourseAssignment> getAssignmentsForStudent(User student) {
        return repository.findByStudent(student);
    }
    
    @Override
    public void deleteAssignment(Long assignmentId) {
        repository.deleteById(assignmentId);
    }

    
    @Override
    public List<CourseAssignment> getAssignmentsByResourceCenter(String resourceCenter) {
        // Get all assignments
        List<CourseAssignment> assignments = repository.findAll();
        
        // Filter assignments by matching student's or faculty's resource center
        return assignments.stream()
            .filter(a -> (a.getStudent() != null && resourceCenter.equalsIgnoreCase(a.getStudent().getResourceCenter())) ||
                        (a.getFaculty() != null && resourceCenter.equalsIgnoreCase(a.getFaculty().getResourceCenter())))
            .collect(Collectors.toList());
    }
    
    @Override
    public CourseAssignment assignCourseToFaculty(User faculty, Course course) {
        CourseAssignment assignment = CourseAssignment.builder()
                .faculty(faculty)
                .course(course)
                .build();
        return repository.save(assignment);
    }
    
    @Override
    public List<CourseAssignment> getAssignmentsForFaculty(User faculty) {
        return repository.findByFaculty(faculty);
    }

    @Override
    public List<Course> getCoursesByFaculty(User faculty) {
        List<CourseAssignment> assignments = repository.findByFaculty(faculty);
        return assignments.stream()
                .map(CourseAssignment::getCourse)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getStudentsByCourse(Course course) {
        List<CourseAssignment> assignments = repository.findByCourse(course);
        return assignments.stream()
                .map(CourseAssignment::getStudent)
                .filter(Objects::nonNull) // Filter out null students (faculty assignments)
                .distinct()
                .collect(Collectors.toList());
    }
}
