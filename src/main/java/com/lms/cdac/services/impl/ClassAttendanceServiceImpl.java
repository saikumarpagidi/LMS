package com.lms.cdac.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.cdac.controller.ClassAttendanceController;
import com.lms.cdac.controller.ClassAttendanceController.AttendanceSubmission;
import com.lms.cdac.entities.ClassAttendance;
import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.User;
import com.lms.cdac.repositories.ClassAttendanceRepository;
import com.lms.cdac.repositories.CourseAssignmentRepository;
import com.lms.cdac.services.ClassAttendanceService;
import com.lms.cdac.services.CourseService;
import com.lms.cdac.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClassAttendanceServiceImpl implements ClassAttendanceService {

    private final ClassAttendanceRepository attendanceRepository;
    private final CourseService courseService;
    private final UserService userService;
    private final CourseAssignmentRepository courseAssignmentRepository;

    @Override
    public List<ClassAttendance> getAttendanceByFacultyAndCourse(User faculty, Course course) {
        return attendanceRepository.findByFacultyAndCourse(faculty, course);
    }

    @Override
    public List<ClassAttendance> getAttendanceByStudent(User student) {
        return attendanceRepository.findByStudent(student);
    }

    @Override
    public List<ClassAttendance> getAttendanceByCourse(Course course) {
        return attendanceRepository.findByCourse(course);
    }

    @Override
    public List<ClassAttendance> getAttendanceByStudentAndCourse(User student, Course course) {
        return attendanceRepository.findByStudentAndCourse(student, course);
    }

    @Override
    public List<ClassAttendance> getAttendanceByFaculty(User faculty) {
        return attendanceRepository.findByFaculty(faculty);
    }

    @Override
    public ClassAttendance saveAttendance(ClassAttendance attendance) {
        return attendanceRepository.save(attendance);
    }

    @Override
    public List<Integer> getAvailableDaysForCourse(Course course) {
        return attendanceRepository.findDistinctDayNumberByCourse(course);
    }

    @Override
    public boolean isAttendanceExistsForCourseAndDay(Integer courseId, Integer dayNumber) {
        Course course = courseService.getCourseById(courseId);
        return !attendanceRepository.findByCourseAndDayNumber(course, dayNumber).isEmpty();
    }

    @Override
    @Transactional
    public void processAttendanceSubmission(AttendanceSubmission submission, User faculty) {
        Course course = courseService.getCourseById(submission.getCourseId());

        // Validate if attendance already exists
        if (isAttendanceExistsForCourseAndDay(submission.getCourseId(), submission.getDayNumber())) {
            throw new IllegalStateException("Attendance already exists for this course and day");
        }

        LocalDateTime now = LocalDateTime.now();
        List<ClassAttendance> attendanceRecords = new ArrayList<>();

        for (ClassAttendanceController.AttendanceRecord record : submission.getAttendanceRecords()) {
            User student = userService.findById(record.getStudentId())
                    .orElseThrow(() -> new IllegalArgumentException("Student not found: " + record.getStudentId()));

            ClassAttendance attendance = new ClassAttendance();
            attendance.setCourse(course);
            attendance.setFaculty(faculty);
            attendance.setStudent(student);
            attendance.setDayNumber(submission.getDayNumber());
            attendance.setPresent(record.isPresent());
            attendance.setAttendanceDate(now);
            attendance.setCourseName(course.getCourseName());
            attendance.setStudentName(student.getName());

            attendanceRecords.add(attendance);
        }

        attendanceRepository.saveAll(attendanceRecords);
    }

    @Override
    public Map<Course, List<ClassAttendance>> getAttendanceMapByFaculty(User faculty) {
        if (!faculty.hasRole("FACULTY")) {
            return new HashMap<>();
        }
        
        List<Course> facultyCourses = courseAssignmentRepository.findByUserWithRole(faculty)
                .stream()
                .map(assignment -> assignment.getCourse())
                .distinct()
                .collect(Collectors.toList());

        Map<Course, List<ClassAttendance>> attendanceMap = new HashMap<>();
        for (Course course : facultyCourses) {
            List<ClassAttendance> courseAttendance = attendanceRepository.findByCourse(course);
            if (!courseAttendance.isEmpty()) {
                attendanceMap.put(course, courseAttendance);
            }
        }

        return attendanceMap;
    }

    @Override
    public List<ClassAttendance> getAttendanceByFacultyAndCourseAndDay(User faculty, Course course, Integer dayNumber) {
        return attendanceRepository.findByFacultyAndCourseAndDayNumber(faculty, course, dayNumber);
    }

    @Override
    public List<ClassAttendance> getAttendanceByCourseIdAndStudentId(Integer courseId, String studentId) {
        return attendanceRepository.findByCourseIdAndStudentUserId(courseId, studentId);
    }
} 