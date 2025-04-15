package com.lms.cdac.controller;

import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.CourseResource;
import com.lms.cdac.services.CourseResourceService;
import com.lms.cdac.services.CourseService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/course-resources")
public class CourseResourceController {

    @Autowired
    private CourseResourceService courseResourceService;

    @Autowired
    private CourseService courseService;  // Assuming you have a CourseService to fetch courses

    // ✅ Show all courses in the dashboard
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        List<Course> courses = courseService.getAllCourses();  // Fetch all courses
        model.addAttribute("courses", courses);  // Pass courses to the dashboard
        return "user/course_dashboard";  // Render course dashboard template
    }

    // ✅ Get Course Resources (For the specific course)
    @GetMapping("/{courseId}")
    public String getCourseResources(@PathVariable Integer courseId, Model model) {
        List<CourseResource> resources = courseResourceService.getResourcesByCourse(courseId);
        String courseName = courseResourceService.getCourseNameById(courseId);

        if (courseName == null) {
            model.addAttribute("errorMessage", "❌ Course not found!");
            return "redirect:/course-resources/dashboard";  // Or some error page
        }

        model.addAttribute("resources", resources);
        model.addAttribute("courseId", courseId);
        model.addAttribute("courseName", courseName);
        return "user/course_resources";  // Render course resource page for specific course
    }

    // ✅ File Upload Handler
    @PostMapping("/upload/{courseId}")
    public String uploadResource(
            @PathVariable Integer courseId,
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Please select a file to upload!");
            return "redirect:/course-resources/" + courseId;
        }

        try {
            courseResourceService.saveResource(courseId, file); // Save the file
            redirectAttributes.addFlashAttribute("successMessage", "✅ File uploaded successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ File upload failed: " + e.getMessage());
        }

        return "redirect:/course-resources/" + courseId;
    }
}



