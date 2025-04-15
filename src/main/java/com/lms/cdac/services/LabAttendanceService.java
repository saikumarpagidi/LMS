package com.lms.cdac.services;

import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.LabAttendance;
import com.lms.cdac.entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface LabAttendanceService {
    
    List<LabAttendance> getAttendanceByStudent(User student);
    
    List<LabAttendance> getAttendanceByCourse(Course course);
    
    List<LabAttendance> getAttendanceByStudentAndCourse(User student, Course course);
    
    List<LabAttendance> getAttendanceByFaculty(User faculty);
    
    LabAttendance saveAttendance(LabAttendance attendance);
    
    Map<String, Object> processCSVUpload(MultipartFile file, User faculty);
    
    boolean isDuplicatePractical(String uniqueKey);
    
    List<LabAttendance> getAttendanceByCourseIdAndStudentId(Integer courseId, String studentId);
} 