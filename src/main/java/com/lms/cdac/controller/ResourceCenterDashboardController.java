package com.lms.cdac.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.lms.cdac.entities.CourseResource;
import com.lms.cdac.entities.CourseSchedule;
import com.lms.cdac.entities.Institution;
import com.lms.cdac.services.CourseResourceService;
import com.lms.cdac.services.CourseScheduleService;
import com.lms.cdac.services.InstitutionService;

@Controller
public class ResourceCenterDashboardController {

    private static final Logger logger = LoggerFactory.getLogger(ResourceCenterDashboardController.class);

    private final CourseScheduleService courseScheduleService;
    private final CourseResourceService courseResourceService;
    private final InstitutionService institutionService;

    // ✅ Constructor Injection
    public ResourceCenterDashboardController(CourseScheduleService courseScheduleService,
                                               CourseResourceService courseResourceService,
                                               InstitutionService institutionService) {
        this.courseScheduleService = courseScheduleService;
        this.courseResourceService = courseResourceService;
        this.institutionService = institutionService;
    }

    // ✅ Resource Centers List Page
    @GetMapping("/resource-center-dashboard")
    public String showResourceCenters(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        List<String> userRoles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        logger.info("🔐 Logged-in User: {}", username);
        logger.info("🎯 User Roles: {}", userRoles);

        List<String> resourceCenters;

        // ADMIN sees all resource centers
        if (userRoles.contains("ROLE_ADMIN")) {
            resourceCenters = List.of("AIIMS New Delhi", "AIIMS Guwahati", "AIIMS Gorakhpur", "AIIMS Patna",
                    "AIIMS Bibinagar", "AIIMS Rishikesh", "AIIMS Madurai", "JIPMER Puducherry", "PGIMER Chandigarh",
                    "SGPGI Lucknow", "AIIMS Jodhpur");

        // PMU_NOIDA role: show resource centers for institutions located in PMU Noida
        } else if (userRoles.contains("ROLE_PMU_NOIDA")) {
            List<Institution> institutions = institutionService.getInstitutionsByLocation("PMU Noida");
            resourceCenters = institutions.stream()
                    .map(Institution::getResourceCenter)
                    .collect(Collectors.toList());
            
        // PMU_MOHALI role: show resource centers for institutions located in PMU Mohali
        } else if (userRoles.contains("ROLE_PMU_MOHALI")) {
            List<Institution> institutions = institutionService.getInstitutionsByLocation("PMU Mohali");
            resourceCenters = institutions.stream()
                    .map(Institution::getResourceCenter)
                    .collect(Collectors.toList());
        } else {
            // Redirect unauthorized users
            return "redirect:/access-denied";
        }

        model.addAttribute("resourceCenters", resourceCenters);
        return "user/resource_center_dashboard";
    }

    // ✅ Show Courses for Selected Resource Center
    @GetMapping("/courses/{resourceCenter}")
    public String getCoursesByResourceCenter(@PathVariable String resourceCenter, Model model) {
        logger.info("📚 Fetching courses for Resource Center: {}", resourceCenter);

        List<CourseSchedule> courses = courseScheduleService.getScheduledCoursesByResourceCenter(resourceCenter);
        if (courses.isEmpty()) {
            logger.warn("⚠️ No courses found for Resource Center: {}", resourceCenter);
        }

        model.addAttribute("courses", courses);
        model.addAttribute("resourceCenter", resourceCenter);
        return "user/courses_fragment";
    }

    // ✅ Show Resources for Selected Course
    @GetMapping("/course/{courseId}/resources")
    public String showCourseResources(@PathVariable Integer courseId, Model model) {
        logger.info("🚀 Requested Course ID: {}", courseId);

        List<CourseResource> resources = courseResourceService.getResourcesByCourse(courseId);
        if (resources.isEmpty()) {
            logger.warn("⚠️ No resources found for Course ID: {}", courseId);
        }

        String courseName = courseResourceService.getCourseNameById(courseId);
        logger.info("📖 Fetched Course Name: {}", courseName);

        model.addAttribute("resources", resources);
        model.addAttribute("courseName", courseName);
        model.addAttribute("courseId", courseId);

        return "user/view_course_resources";
    }
}
