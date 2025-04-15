package com.lms.cdac.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.CourseModule;
import com.lms.cdac.services.CourseModuleService;
import com.lms.cdac.services.CourseService;
import com.lms.cdac.services.CourseResourceService;
import com.lms.cdac.services.CourseTopicService;

@Controller
@RequestMapping("/course-design")
public class CourseDesignController {

    private final CourseService courseService;
    private final CourseModuleService courseModuleService;
    private final CourseTopicService topicService;
    private final CourseResourceService resourceService;

    public CourseDesignController(CourseService courseService, CourseModuleService courseModuleService, 
                                  CourseTopicService topicService, CourseResourceService resourceService) {
        this.courseService = courseService;
        this.courseModuleService = courseModuleService;
        this.topicService = topicService;
        this.resourceService = resourceService;
    }

    @GetMapping
    public String courseDesign(@RequestParam(value = "courseId", required = false) Integer courseId, Model model, RedirectAttributes redirectAttributes) {
        List<Course> allCourses = courseService.getAllCourses();
        model.addAttribute("allCourses", allCourses);

        if (courseId != null) {
            try {
                Course course = courseService.getCourseById(courseId);
                if (course == null) {
                    redirectAttributes.addFlashAttribute("errorMessage", "❌ Course not found!");
                    return "redirect:/course-design";
                }
                List<CourseModule> modules = courseModuleService.getModulesByCourseId(courseId);
                model.addAttribute("course", course);
                model.addAttribute("modules", modules);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "❌ Error loading course: " + e.getMessage());
                return "redirect:/course-design";
            }
        }
        return "course_design";
    }

    @PostMapping("/add-module")
    public String addModule(@RequestParam Integer courseId, @RequestParam String moduleName, RedirectAttributes redirectAttributes) {
        if (courseId == null || moduleName.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Course ID and Module Name are required!");
            return "redirect:/course-design";
        }
        try {
            // Check if the course actually exists
            Course course = courseService.getCourseById(courseId);
            if (course == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "❌ Course not found!");
                return "redirect:/course-design";
            }
            courseModuleService.addModule(courseId, moduleName);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Module added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Failed to add module: " + e.getMessage());
        }
        return "redirect:/course-design?courseId=" + courseId;
    }

    @PostMapping("/add-topic")
    public String addTopic(@RequestParam Integer moduleId, @RequestParam String topicName, RedirectAttributes redirectAttributes) {
        if (moduleId == null || topicName.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Module ID and Topic Name are required!");
            return "redirect:/course-design";
        }
        
        try {
            topicService.addTopic(moduleId, topicName);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Topic added successfully!");
            Integer courseIdFromModule = courseModuleService.getCourseIdByModuleId(moduleId);
            return "redirect:/course-design?courseId=" + courseIdFromModule;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Failed to add topic: " + e.getMessage());
            return "redirect:/course-design";
        }
    }

    @PostMapping("/upload-resource")
    public String uploadResource(@RequestParam Integer topicId, 
                                 @RequestParam MultipartFile file, 
                                 RedirectAttributes redirectAttributes) throws IOException {
        if (topicId == null || file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Please select a valid file to upload!");
            return "redirect:/course-design";
        }
        try {
            resourceService.saveResource(topicId, file);
            redirectAttributes.addFlashAttribute("successMessage", "✅ File uploaded successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ File upload failed: " + e.getMessage());
        }
        Integer courseIdFromTopic = topicService.getCourseIdByTopicId(topicId);
        return "redirect:/course-design?courseId=" + courseIdFromTopic;
    }

    @GetMapping("/view-resource")
    public ResponseEntity<Resource> viewResource(@RequestParam Integer resourceId) {
        try {
            // Database se `file_url` retrieve karein
        	String fileUrl= resourceService.getResourcePathById(resourceId);

            if (fileUrl == null || fileUrl.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Debugging ke liye print karein (Agar problem aaye to check karein)
            System.out.println("File URL from DB: " + fileUrl);

            // File ko load karein
            Path path = Paths.get(fileUrl);
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // File type detect karein
            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + path.getFileName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
