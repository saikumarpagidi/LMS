package com.lms.cdac.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.User;
import com.lms.cdac.services.CourseService;

@Controller
@RequestMapping("/course")
public class CourseController {
    
    @Autowired
    private CourseService courseService;

    @GetMapping("/create")
    public String showCourseForm(Model model) {
        model.addAttribute("course", new Course());
        return "user/course-create";
    }

    @PostMapping("/save")
    public String saveCourse(@ModelAttribute Course course, Authentication authentication, RedirectAttributes redirectAttributes) {
        // Get logged in user
        User user = (User) authentication.getPrincipal();
        
        // Set the resource center from logged in user
//        course.setResourceCenter(user.getResourceCenter());
        
        courseService.saveCourse(course);
        
        // Add success message
        redirectAttributes.addFlashAttribute("successMessage", "✅ Course created successfully!");

        return "redirect:/course/create";
    }
}
