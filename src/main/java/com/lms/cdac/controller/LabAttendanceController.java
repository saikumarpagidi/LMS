package com.lms.cdac.controller;

import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.LabAttendance;
import com.lms.cdac.entities.ClassAttendance;
import com.lms.cdac.entities.User;
import com.lms.cdac.services.CourseService;
import com.lms.cdac.services.LabAttendanceService;
import com.lms.cdac.services.ClassAttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/attendance")
public class LabAttendanceController {

    @Autowired
    private LabAttendanceService labAttendanceService;
    
    @Autowired
    private ClassAttendanceService classAttendanceService;
    
    @Autowired
    private CourseService courseService;

    // Faculty: Upload attendance page
    @GetMapping("/upload")
    public String showUploadPage(Model model, Authentication authentication) {
        User faculty = (User) authentication.getPrincipal();
        if (!faculty.hasRole("FACULTY")) {
            return "redirect:/access-denied";
        }
        
        List<Course> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);
        return "faculty/upload-attendance";
    }
    
    // Faculty: Process CSV upload
    @PostMapping("/upload")
    public String processUpload(@RequestParam("file") MultipartFile file, 
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        User faculty = (User) authentication.getPrincipal();
        if (!faculty.hasRole("FACULTY")) {
            return "redirect:/access-denied";
        }
        
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select a file to upload");
            return "redirect:/attendance/upload";
        }
        
        if (!file.getOriginalFilename().endsWith(".csv")) {
            redirectAttributes.addFlashAttribute("error", "Only CSV files are allowed");
            return "redirect:/attendance/upload";
        }
        
        Map<String, Object> result = labAttendanceService.processCSVUpload(file, faculty);
        
        if ((Boolean) result.get("success")) {
            redirectAttributes.addFlashAttribute("message", result.get("message"));
        } else {
            redirectAttributes.addFlashAttribute("error", result.get("message"));
            if (result.containsKey("errors")) {
                redirectAttributes.addFlashAttribute("errors", result.get("errors"));
            }
        }
        
        return "redirect:/attendance/upload";
    }
    
    // Faculty: View uploaded attendance
    @GetMapping("/faculty/view")
    public String viewFacultyUploads(Model model, Authentication authentication) {
        User faculty = (User) authentication.getPrincipal();
        if (!faculty.hasRole("FACULTY")) {
            return "redirect:/access-denied";
        }
        
        List<LabAttendance> attendances = labAttendanceService.getAttendanceByFaculty(faculty);
        model.addAttribute("attendances", attendances);
        return "faculty/view-attendance";
    }
    
    // Student: View attendance for a specific course
    @GetMapping("/student/course/{courseId}")
    public String viewStudentCourseAttendance(@PathVariable Integer courseId, 
                                             Model model, 
                                             Authentication authentication) {
        User student = (User) authentication.getPrincipal();
        if (!student.hasRole("STUDENT")) {
            return "redirect:/access-denied";
        }
        
        // Get lab attendance records
        List<LabAttendance> labAttendances = labAttendanceService.getAttendanceByCourseIdAndStudentId(courseId, student.getUserId());
        model.addAttribute("labAttendances", labAttendances);
        
        // Get class attendance records
        List<ClassAttendance> classAttendances = classAttendanceService.getAttendanceByCourseIdAndStudentId(courseId, student.getUserId());
        model.addAttribute("classAttendances", classAttendances);
        
        // Get course and add it to the model
        Course course = courseService.getCourseById(courseId);
        model.addAttribute("course", course);
        
        return "Student/view-attendance";
    }
} 