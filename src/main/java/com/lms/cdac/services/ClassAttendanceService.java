package com.lms.cdac.services;

import java.util.List;
import java.util.Map;

import com.lms.cdac.controller.ClassAttendanceController.AttendanceSubmission;
import com.lms.cdac.entities.ClassAttendance;
import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.User;

public interface ClassAttendanceService {
    
    List<ClassAttendance> getAttendanceByFacultyAndCourse(User faculty, Course course);
    
    List<ClassAttendance> getAttendanceByStudent(User student);
    
    List<ClassAttendance> getAttendanceByCourse(Course course);
    
    List<ClassAttendance> getAttendanceByStudentAndCourse(User student, Course course);
    
    List<ClassAttendance> getAttendanceByFaculty(User faculty);
    
    ClassAttendance saveAttendance(ClassAttendance attendance);
    
    List<Integer> getAvailableDaysForCourse(Course course);
    
    boolean isAttendanceExistsForCourseAndDay(Integer courseId, Integer dayNumber);
    
    void processAttendanceSubmission(AttendanceSubmission submission, User faculty);
    
    Map<Course, List<ClassAttendance>> getAttendanceMapByFaculty(User faculty);
    
    List<ClassAttendance> getAttendanceByFacultyAndCourseAndDay(User faculty, Course course, Integer dayNumber);
    
    List<ClassAttendance> getAttendanceByCourseIdAndStudentId(Integer courseId, String studentId);
} 