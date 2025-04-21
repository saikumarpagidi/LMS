package com.lms.cdac.repositories;

import com.lms.cdac.entities.ClassAttendance;
import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassAttendanceRepository extends JpaRepository<ClassAttendance, Integer> {
    
    List<ClassAttendance> findByCourseAndDayNumber(Course course, Integer dayNumber);
    
    List<ClassAttendance> findByStudentAndCourse(User student, Course course);
    
    List<ClassAttendance> findByFacultyAndCourse(User faculty, Course course);
    
    List<ClassAttendance> findByCourseIdAndDayNumber(Integer courseId, Integer dayNumber);
    
    boolean existsByStudentAndCourseAndDayNumber(User student, Course course, Integer dayNumber);
    
    List<ClassAttendance> findByStudent(User student);
    
    List<ClassAttendance> findByCourse(Course course);
    
    List<ClassAttendance> findByFaculty(User faculty);
    
    @Query("SELECT DISTINCT a.dayNumber FROM ClassAttendance a WHERE a.course = :course")
    List<Integer> findDistinctDayNumberByCourse(Course course);

    List<ClassAttendance> findByFacultyAndCourseAndDayNumber(User faculty, Course course, Integer dayNumber);

    List<ClassAttendance> findByCourseIdAndStudentUserId(Integer courseId, String studentId);
} 