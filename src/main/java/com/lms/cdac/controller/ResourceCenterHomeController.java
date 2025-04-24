package com.lms.cdac.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.CourseModule;
import com.lms.cdac.entities.CourseResource;
import com.lms.cdac.entities.CourseSchedule;
import com.lms.cdac.entities.CourseTopic;
import com.lms.cdac.entities.CourseAssignment;
import com.lms.cdac.entities.User;
import com.lms.cdac.repsitories.UserRepositories;
import com.lms.cdac.services.CourseAssignmentService;
import com.lms.cdac.services.CourseModuleService;
import com.lms.cdac.services.CourseResourceService;
import com.lms.cdac.services.CourseScheduleService;
import com.lms.cdac.services.CourseService;
import com.lms.cdac.services.CourseTopicService;

@Controller
public class ResourceCenterHomeController {

    private static final Logger logger = LoggerFactory.getLogger(ResourceCenterHomeController.class);

    private final CourseScheduleService courseScheduleService;
    private final CourseService courseService;
    private final UserRepositories userRepositories;
    private final CourseAssignmentService courseAssignmentService;
    private final CourseResourceService courseResourceService;
    private final CourseModuleService courseModuleService;
    private final CourseTopicService courseTopicService;

    public ResourceCenterHomeController(CourseScheduleService courseScheduleService, CourseService courseService,
            UserRepositories userRepositories, CourseAssignmentService courseAssignmentService,
            CourseResourceService courseResourceService, CourseModuleService courseModuleService,
            CourseTopicService courseTopicService) {
        this.courseScheduleService = courseScheduleService;
        this.courseService = courseService;
        this.userRepositories = userRepositories;
        this.courseAssignmentService = courseAssignmentService;
        this.courseResourceService = courseResourceService;
        this.courseModuleService = courseModuleService;
        this.courseTopicService = courseTopicService;
    }

    // Utility method to validate resource center access
    private User validateResourceCenterAccess(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (!user.hasRole("RESOURCE_CENTER")) {
            logger.warn("User does not have RESOURCE_CENTER role: {}", user.getEmail());
            throw new RuntimeException("Access denied. User does not have resource center role.");
        }
        return user;
    }
    
    // Utility method to prepare common model attributes
    private void prepareCommonModelAttributes(Model model, String resourceCenter) {
        // Fetch scheduled courses from the CourseSchedule entity
        List<CourseSchedule> coursesSchedule = courseScheduleService
                .getScheduledCoursesByResourceCenter(resourceCenter);
        
        // Derive available courses (distinct Course objects) from schedules
        List<Course> availableCourses = coursesSchedule.stream()
                .map(CourseSchedule::getCourse)
                .distinct()
                .collect(Collectors.toList());
        
        // Enhance courses with resources, modules and topics
        coursesSchedule.forEach(schedule -> {
            Course course = schedule.getCourse();
            
            // Add resources to each course
            List<CourseResource> resources = courseResourceService.getResourcesByCourse(course.getId());
            course.setResources(resources != null ? resources : new ArrayList<>());
            
            // Load modules for each course
            List<CourseModule> modules = courseModuleService.getModulesByCourseId(course.getId());
            course.setModules(modules != null ? modules : new ArrayList<>());
            
            // Load topics for each course (collect from all modules)
            List<CourseTopic> allTopics = new ArrayList<>();
            if (modules != null) {
                for (CourseModule module : modules) {
                    List<CourseTopic> topics = courseTopicService.getTopicsByModule(module.getId());
                    if (topics != null) {
                        module.setTopics(topics);
                        allTopics.addAll(topics);
                    }
                }
            }
            course.setTopics(allTopics);
        });
        
        // Filter students with STUDENT role whose resourceCenter matches
        List<User> students = userRepositories.findAll().stream()
                .filter(u -> u.hasRole("STUDENT") && resourceCenter.equalsIgnoreCase(u.getResourceCenter()))
                .collect(Collectors.toList());
        
        // Filter faculty with FACULTY role whose resourceCenter matches
        List<User> faculty = userRepositories.findAll().stream()
                .filter(u -> u.hasRole("FACULTY") && resourceCenter.equalsIgnoreCase(u.getResourceCenter()))
                .collect(Collectors.toList());
        
        model.addAttribute("courses", coursesSchedule);
        model.addAttribute("availableCourses", availableCourses);
        model.addAttribute("students", students);
        model.addAttribute("faculty", faculty);
        model.addAttribute("resourceCenter", resourceCenter);
    }

    // Main dashboard (simplified to only welcome message)
    @GetMapping("/center/dashboard")
    public String showDashboard(Model model, Authentication authentication) {
        try {
            User admin = validateResourceCenterAccess(authentication);
            String resourceCenter = (admin.getResourceCenter() != null) ? admin.getResourceCenter().trim() : "";
            logger.info("Dashboard accessed by: {} | Resource Center: {}", admin.getEmail(), resourceCenter);
            
            model.addAttribute("resourceCenter", resourceCenter);
            return "resource-center/RC-dashboard";
        } catch (Exception e) {
            logger.error("Error accessing dashboard: {}", e.getMessage());
            return "redirect:/user/dashboard";
        }
    }

    // Course details page
    @GetMapping("/center/courses")
    public String showCourseDetails(Model model, Authentication authentication) {
        try {
            User admin = validateResourceCenterAccess(authentication);
            String resourceCenter = (admin.getResourceCenter() != null) ? admin.getResourceCenter().trim() : "";
            logger.info("Course details accessed by: {} | Resource Center: {}", admin.getEmail(), resourceCenter);
            
            prepareCommonModelAttributes(model, resourceCenter);
            
            return "resource-center/course-details";
        } catch (Exception e) {
            logger.error("Error accessing course details: {}", e.getMessage());
            return "redirect:/center/dashboard";
        }
    }
    
    // Assign course page
    @GetMapping("/center/assign-course")
    public String showAssignCoursePage(Model model, Authentication authentication) {
        try {
            User admin = validateResourceCenterAccess(authentication);
            String resourceCenter = (admin.getResourceCenter() != null) ? admin.getResourceCenter().trim() : "";
            logger.info("Assign course page accessed by: {} | Resource Center: {}", admin.getEmail(), resourceCenter);
            
            prepareCommonModelAttributes(model, resourceCenter);
            
            return "resource-center/assign-course";
        } catch (Exception e) {
            logger.error("Error accessing assign course page: {}", e.getMessage());
            return "redirect:/center/dashboard";
        }
    }
    
    // Registered students page
    @GetMapping("/center/students")
    public String showRegisteredStudents(Model model, Authentication authentication) {
        try {
            User admin = validateResourceCenterAccess(authentication);
            String resourceCenter = (admin.getResourceCenter() != null) ? admin.getResourceCenter().trim() : "";
            logger.info("Registered students accessed by: {} | Resource Center: {}", admin.getEmail(), resourceCenter);
            
            // Simple version that just fetches basic student information
            List<User> students = userRepositories.findAll().stream()
                    .filter(u -> u.hasRole("STUDENT") && resourceCenter.equalsIgnoreCase(u.getResourceCenter()))
                    .collect(Collectors.toList());
            
            logger.info("Number of students found: {}", students.size());
            model.addAttribute("students", students);
            model.addAttribute("resourceCenter", resourceCenter);
            
            return "resource-center/registered-students";
        } catch (Exception e) {
            logger.error("Error accessing registered students: {}", e.getMessage());
            return "redirect:/center/dashboard";
        }
    }
    
    // Registered faculty page
    @GetMapping("/center/faculty")
    public String showRegisteredFaculty(Model model, Authentication authentication) {
        try {
            User admin = validateResourceCenterAccess(authentication);
            String resourceCenter = (admin.getResourceCenter() != null) ? admin.getResourceCenter().trim() : "";
            logger.info("Registered faculty accessed by: {} | Resource Center: {}", admin.getEmail(), resourceCenter);
            
            // Fetch faculty with FACULTY role whose resourceCenter matches
            List<User> faculty = userRepositories.findAll().stream()
                    .filter(u -> u.hasRole("FACULTY") && resourceCenter.equalsIgnoreCase(u.getResourceCenter()))
                    .collect(Collectors.toList());
            
            logger.info("Number of faculty found: {}", faculty.size());
            model.addAttribute("faculty", faculty);
            model.addAttribute("resourceCenter", resourceCenter);
            
            return "resource-center/registered-faculty";
        } catch (Exception e) {
            logger.error("Error accessing registered faculty: {}", e.getMessage());
            return "redirect:/center/dashboard";
        }
    }

    // Assign course functionality
    @PostMapping("/center/dashboard/assign")
    public String assignCourse(@RequestParam("studentId") String studentId, 
                               @RequestParam("courseId") Integer courseId,
                               RedirectAttributes redirectAttributes, 
                               Authentication authentication) {
        try {
            User admin = validateResourceCenterAccess(authentication);
            String resourceCenter = (admin.getResourceCenter() != null) ? admin.getResourceCenter().trim() : "";

            User student = userRepositories.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            Optional<Course> courseOpt = courseService.findById(courseId);
            Course course = courseOpt.orElseThrow(() -> new RuntimeException("Course not found"));

            courseAssignmentService.assignCourseToStudent(student, course);
            redirectAttributes.addFlashAttribute("message", "Course assigned successfully.");
            
            return "redirect:/center/assign-course";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Assignment failed: " + e.getMessage());
            return "redirect:/center/assign-course";
        }
    }
    
    // Assign course to faculty functionality
    @PostMapping("/center/dashboard/assign-faculty")
    public String assignCourseToFaculty(@RequestParam("facultyId") String facultyId, 
                                       @RequestParam("courseId") Integer courseId,
                                       RedirectAttributes redirectAttributes, 
                                       Authentication authentication) {
        try {
            User admin = validateResourceCenterAccess(authentication);
            String resourceCenter = (admin.getResourceCenter() != null) ? admin.getResourceCenter().trim() : "";

            User faculty = userRepositories.findById(facultyId)
                    .orElseThrow(() -> new RuntimeException("Faculty not found"));
            
            // Verify faculty belongs to the resource center
            if (!resourceCenter.equalsIgnoreCase(faculty.getResourceCenter())) {
                throw new RuntimeException("Faculty does not belong to this resource center");
            }
            
            Optional<Course> courseOpt = courseService.findById(courseId);
            Course course = courseOpt.orElseThrow(() -> new RuntimeException("Course not found"));

            // Check if the course is already assigned to this faculty
            List<CourseAssignment> existingAssignments = courseAssignmentService.getAssignmentsForFaculty(faculty);
            boolean alreadyAssigned = existingAssignments.stream()
                    .anyMatch(a -> a.getCourse().getId().equals(courseId));
            
            if (alreadyAssigned) {
                redirectAttributes.addFlashAttribute("error", "Course is already assigned to this faculty member.");
                return "redirect:/center/assign-faculty";
            }

            courseAssignmentService.assignCourseToFaculty(faculty, course);
            redirectAttributes.addFlashAttribute("message", "Course assigned to faculty successfully.");
            
            return "redirect:/center/assign-faculty";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Assignment failed: " + e.getMessage());
            return "redirect:/center/assign-faculty";
        }
    }
    
    // Assign faculty page
    @GetMapping("/center/assign-faculty")
    public String showAssignFacultyPage(Model model, Authentication authentication) {
        try {
            User admin = validateResourceCenterAccess(authentication);
            String resourceCenter = (admin.getResourceCenter() != null) ? admin.getResourceCenter().trim() : "";
            logger.info("Assign faculty page accessed by: {} | Resource Center: {}", admin.getEmail(), resourceCenter);
            
            prepareCommonModelAttributes(model, resourceCenter);
            
            return "resource-center/assign-faculty";
        } catch (Exception e) {
            logger.error("Error accessing assign faculty page: {}", e.getMessage());
            return "redirect:/center/dashboard";
        }
    }

    // Course syllabus page
    @GetMapping("/center/course-syllabus/{courseId}")
    public String showCourseSyllabus(@PathVariable Integer courseId, 
                                   Model model, 
                                   Authentication authentication) {
        try {
            User admin = validateResourceCenterAccess(authentication);
            String resourceCenter = (admin.getResourceCenter() != null) ? admin.getResourceCenter().trim() : "";
            logger.info("Course syllabus accessed by: {} | Resource Center: {} | Course ID: {}", 
                       admin.getEmail(), resourceCenter, courseId);
            
            // Fetch course with all related data
            Course course = courseService.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));
            
//            // Verify course belongs to the resource center - only if resourceCenter is set on both
//            if (course.getResourceCenter() != null && !course.getResourceCenter().trim().isEmpty() &&
//                resourceCenter != null && !resourceCenter.trim().isEmpty() &&
//                !course.getResourceCenter().equalsIgnoreCase(resourceCenter)) {
//                throw new RuntimeException("Course does not belong to this resource center");
//            }
            
            // Load modules for the course
            List<CourseModule> modules = courseModuleService.getModulesByCourseId(course.getId());
            course.setModules(modules != null ? modules : new ArrayList<>());
            
            // Load topics for each module
            if (course.getModules() != null) {
                for (CourseModule module : course.getModules()) {
                    // Get topics for the module
                    List<CourseTopic> topics = courseTopicService.getTopicsByModule(module.getId());
                    module.setTopics(topics != null ? topics : new ArrayList<>());
                    
                    // If needed, load resources for each topic
                    if (topics != null) {
                        for (CourseTopic topic : topics) {
                            List<CourseResource> resources = courseResourceService.getResourcesByTopic(topic.getId());
                            topic.setResources(resources != null ? resources : new ArrayList<>());
                        }
                    }
                }
            }
            
            // Add course to model
            model.addAttribute("course", course);
            model.addAttribute("resourceCenter", resourceCenter);
            
            return "resource-center/course-syllabus";
        } catch (Exception e) {
            logger.error("Error accessing course syllabus: {}", e.getMessage(), e);
            return "redirect:/center/courses";
        }
    }
    
    //  Endpoint: View all assigned courses page
    @GetMapping("/center/assigned-courses")
    public String viewAssignedCourses(Model model, Authentication authentication) {
        try {
            User admin = validateResourceCenterAccess(authentication);
            String resourceCenter = (admin.getResourceCenter() != null) ? admin.getResourceCenter().trim() : "";
            
            // Get assignments filtered by resource center
            List<CourseAssignment> assignments = courseAssignmentService.getAssignmentsByResourceCenter(resourceCenter);
            model.addAttribute("assignments", assignments);
            model.addAttribute("resourceCenter", resourceCenter);
            return "resource-center/assigned-courses";
        } catch (Exception e) {
            logger.error("Error accessing assigned courses: {}", e.getMessage());
            return "redirect:/center/dashboard";
        }
    }
    
    //  Endpoint: Delete an assignment
    @PostMapping("/center/assigned-courses/delete")
    public String deleteAssignment(@RequestParam("assignmentId") Long assignmentId,
                                   RedirectAttributes redirectAttributes,
                                   Authentication authentication) {
        try {
            validateResourceCenterAccess(authentication);
            courseAssignmentService.deleteAssignment(assignmentId);
            redirectAttributes.addFlashAttribute("message", "Assignment deleted successfully.");
            return "redirect:/center/assigned-courses";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete assignment: " + e.getMessage());
            return "redirect:/center/assigned-courses";
        }
    }

}
