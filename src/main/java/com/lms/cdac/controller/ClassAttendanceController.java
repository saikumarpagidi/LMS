package com.lms.cdac.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.ClassAttendance;
import com.lms.cdac.entities.User;
import com.lms.cdac.services.CourseAssignmentService;
import com.lms.cdac.services.ClassAttendanceService;
import com.lms.cdac.services.CourseService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/class-attendance")
public class ClassAttendanceController {

    private final ClassAttendanceService classAttendanceService;
    private final CourseAssignmentService courseAssignmentService;
    private final CourseService courseService;

    // Show upload form
    @GetMapping("/upload")
    public String showUploadForm(Model model, Authentication authentication) {
        User faculty = (User) authentication.getPrincipal();
        if (!faculty.hasRole("FACULTY")) {
            return "redirect:/access-denied";
        }
        
        List<Course> assignedCourses = courseAssignmentService.getCoursesByFaculty(faculty);
        model.addAttribute("courses", assignedCourses);
        return "faculty/upload-class-attendance";
    }
    
    // Generate attendance sheet
    @PostMapping("/generate-sheet")
    public String generateAttendanceSheet(@RequestParam Integer courseId,
                                        @RequestParam Integer dayNumber,
                                        Model model,
                                        Authentication authentication,
                                        RedirectAttributes redirectAttributes) {
        User faculty = (User) authentication.getPrincipal();
        if (!faculty.hasRole("FACULTY")) {
            return "redirect:/access-denied";
        }
        
        try {
            // Check if attendance already exists for this course and day
            if (classAttendanceService.isAttendanceExistsForCourseAndDay(courseId, dayNumber)) {
                redirectAttributes.addFlashAttribute("error", 
                    "Attendance for this course and day has already been recorded.");
                return "redirect:/class-attendance/upload";
            }
            
            Course course = courseService.getCourseById(courseId);
            if (course == null) {
                redirectAttributes.addFlashAttribute("error", "Course not found.");
                return "redirect:/class-attendance/upload";
            }
            
            // Validate day number against course duration
            if (dayNumber > course.getDuration()) {
                redirectAttributes.addFlashAttribute("error", 
                    "Day number cannot exceed course duration of " + course.getDuration() + " days.");
                return "redirect:/class-attendance/upload";
            }
            
            // Get students enrolled in the course
            List<User> students = courseAssignmentService.getStudentsByCourse(course);
            if (students.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", 
                    "No students are enrolled in this course.");
                return "redirect:/class-attendance/upload";
            }
            
            // Add all necessary attributes to the model
            model.addAttribute("courses", courseAssignmentService.getCoursesByFaculty(faculty));
            model.addAttribute("selectedCourse", course);
            model.addAttribute("students", students);
            model.addAttribute("dayNumber", dayNumber);
            
            return "faculty/upload-class-attendance";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "An error occurred while generating the attendance sheet: " + e.getMessage());
            return "redirect:/class-attendance/upload";
        }
    }
    
    @Data
    public static class AttendanceRecord {
        private String studentId;
        private boolean present;
    }
    
    @Data
    public static class AttendanceSubmission {
        private Integer courseId;
        private Integer dayNumber;
        private List<AttendanceRecord> attendanceRecords;
    }
    
    // Submit attendance
    @PostMapping("/submit")
    public String submitAttendance(@ModelAttribute AttendanceSubmission submission,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        User faculty = (User) authentication.getPrincipal();
        if (!faculty.hasRole("FACULTY")) {
            return "redirect:/access-denied";
        }
        
        try {
            // Validate submission
            if (submission.getCourseId() == null || submission.getDayNumber() == null 
                || submission.getAttendanceRecords() == null || submission.getAttendanceRecords().isEmpty()) {
                throw new IllegalArgumentException("Invalid attendance submission data");
            }
            
            // Check for duplicate attendance
            if (classAttendanceService.isAttendanceExistsForCourseAndDay(
                    submission.getCourseId(), submission.getDayNumber())) {
                throw new IllegalStateException("Attendance already exists for this course and day");
            }
            
            // Process attendance records
            classAttendanceService.processAttendanceSubmission(submission, faculty);
            
            redirectAttributes.addFlashAttribute("message", "Attendance recorded successfully!");
            return "redirect:/class-attendance/faculty/view";
            
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/class-attendance/upload";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "An error occurred while saving attendance: " + e.getMessage());
            return "redirect:/class-attendance/upload";
        }
    }

    @GetMapping("/faculty/view")
    public String showAttendanceView(Model model, Authentication authentication) {
        User faculty = (User) authentication.getPrincipal();
        if (!faculty.hasRole("FACULTY")) {
            return "redirect:/access-denied";
        }

        // Get all courses assigned to the faculty
        List<Course> facultyCourses = courseAssignmentService.getCoursesByFaculty(faculty);
        model.addAttribute("courses", facultyCourses);

        return "faculty/view-class-attendance";
    }

    @PostMapping("/faculty/view")
    public String viewAttendanceForCourseAndDay(
            @RequestParam("courseId") Integer courseId,
            @RequestParam("dayNumber") Integer dayNumber,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        User faculty = (User) authentication.getPrincipal();
        if (!faculty.hasRole("FACULTY")) {
            return "redirect:/access-denied";
        }

        try {
            // Get the course
            Course course = courseService.getCourseById(courseId);
            
            // Validate day number
            if (dayNumber < 1 || dayNumber > course.getDuration()) {
                redirectAttributes.addFlashAttribute("error", "Invalid day number. Please select a day between 1 and " + course.getDuration());
                return "redirect:/class-attendance/faculty/view";
            }

            // Get attendance records for the selected course and day
            List<ClassAttendance> attendanceRecords = classAttendanceService.getAttendanceByFacultyAndCourseAndDay(faculty, course, dayNumber);
            
            // Get all courses for the dropdown
            List<Course> facultyCourses = courseAssignmentService.getCoursesByFaculty(faculty);
            
            model.addAttribute("courses", facultyCourses);
            model.addAttribute("selectedCourse", course);
            model.addAttribute("selectedDay", dayNumber);
            model.addAttribute("attendanceRecords", attendanceRecords);
            model.addAttribute("courseDuration", course.getDuration());
            
            return "faculty/view-class-attendance";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error viewing attendance: " + e.getMessage());
            return "redirect:/class-attendance/faculty/view";
        }
    }
} 