package com.lms.cdac.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lms.cdac.entities.Badge;
import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.CourseAssignment;
import com.lms.cdac.entities.CourseModule;
import com.lms.cdac.entities.CourseProgress;
import com.lms.cdac.entities.CourseResource;
import com.lms.cdac.entities.CourseSchedule;
import com.lms.cdac.entities.CourseTopic;
import com.lms.cdac.entities.QuizAssignment;
import com.lms.cdac.entities.User;
import com.lms.cdac.entities.LiveClassSchedule;
import com.lms.cdac.services.BadgeService;
import com.lms.cdac.services.CourseAssignmentService;
import com.lms.cdac.services.CourseModuleService;
import com.lms.cdac.services.CourseProgressService;
import com.lms.cdac.services.CourseResourceService;
import com.lms.cdac.services.CourseScheduleService;
import com.lms.cdac.services.CourseService;
import com.lms.cdac.services.CourseTopicService;
import com.lms.cdac.services.QuizAssignmentService;
import com.lms.cdac.services.LiveClassScheduleService;
import java.util.stream.Collectors;
import java.time.LocalDateTime;


@Controller
public class StudentDashboardController {

    private final CourseAssignmentService courseAssignmentService;
    private final CourseResourceService courseResourceService;
    private final CourseScheduleService courseScheduleService;
    private final CourseService courseService;
    private final CourseModuleService courseModuleService;
    private final CourseTopicService courseTopicService;
    private final QuizAssignmentService quizAssignmentService;
    private final CourseProgressService courseProgressService;
    private final BadgeService badgeService;
    private final LiveClassScheduleService liveClassScheduleService;

    public StudentDashboardController(
            CourseAssignmentService courseAssignmentService,
            CourseResourceService courseResourceService,
            CourseScheduleService courseScheduleService,
            CourseService courseService,
            CourseModuleService courseModuleService,
            CourseTopicService courseTopicService,
            QuizAssignmentService quizAssignmentService,
            CourseProgressService courseProgressService,
            BadgeService badgeService,
            LiveClassScheduleService liveClassScheduleService) {
        this.courseAssignmentService = courseAssignmentService;
        this.courseResourceService = courseResourceService;
        this.courseScheduleService = courseScheduleService;
        this.courseService = courseService;
        this.courseModuleService = courseModuleService;
        this.courseTopicService = courseTopicService;
        this.quizAssignmentService = quizAssignmentService;
        this.courseProgressService = courseProgressService;
        this.badgeService = badgeService;
        this.liveClassScheduleService = liveClassScheduleService;
    }

    @GetMapping("/student/dashboard")
    public String studentDashboard(Model model, Authentication authentication) {
        User student = (User) authentication.getPrincipal();
        if (!student.hasRole("STUDENT")) {
            return "redirect:/user/dashboard";
        }

        List<CourseAssignment> assignments = courseAssignmentService.getAssignmentsForStudent(student);

        Map<Integer, CourseSchedule> scheduleMap = new HashMap<>();
        Map<Integer, Double> progressMap = new HashMap<>();
        Map<Integer, Badge> badgeMap = new HashMap<>();
        Map<Integer, Double> overallProgressMap = new HashMap<>();

        List<Integer> courseIds = assignments.stream()
                .map(a -> a.getCourse().getId())
                .collect(Collectors.toList());

        Map<Integer, List<LiveClassSchedule>> liveClassMap = new HashMap<>();
        if (!courseIds.isEmpty()) {
            List<LiveClassSchedule> allLive = liveClassScheduleService.getLiveClassesByCourseIds(courseIds);
            liveClassMap = allLive.stream()
                    .collect(Collectors.groupingBy(LiveClassSchedule::getCourseId));
        }

        assignments.forEach(assignment -> {
            Integer courseId = assignment.getCourse().getId();
            Course course = assignment.getCourse();

            List<CourseResource> resources = courseResourceService.getResourcesByCourse(courseId);
            course.setResources(resources);
            resources.forEach(resource -> {
                Optional<CourseProgress> progressOpt = courseProgressService.getProgress(
                        student.getUserId(), courseId, resource.getId());
                resource.setProgress(progressOpt.map(CourseProgress::getProgressPercentage).orElse(0.0));
            });

            List<CourseSchedule> schedules = courseScheduleService.getScheduleByCourseId(courseId);
            if (!schedules.isEmpty()) {
                scheduleMap.put(courseId, schedules.get(0));
            }

            double overallProgress = courseProgressService.calculateOverallProgress(
                    student.getUserId(), courseId);
            progressMap.put(courseId, overallProgress);

            Badge badge = badgeService.calculateBadge(student, course);
            badgeMap.put(courseId, badge);

            double weightedProg = badgeService.calculateOverallProgress(student, course);
            overallProgressMap.put(courseId, weightedProg);
        });

        model.addAttribute("assignments", assignments);
        model.addAttribute("scheduleMap", scheduleMap);
        model.addAttribute("progressMap", progressMap);
        model.addAttribute("badgeMap", badgeMap);
        model.addAttribute("overallProgressMap", overallProgressMap);
        model.addAttribute("liveClassMap", liveClassMap);
        return "student-dashboard";
    }

    @PostMapping("/student/update-progress")
    @ResponseBody
    public ResponseEntity<String> updateCourseProgress(@RequestBody Map<String, Object> payload,
            Authentication authentication) {
        try {
            User student = (User) authentication.getPrincipal();
            Integer courseId = Integer.parseInt(payload.get("courseId").toString());
            Integer resourceId = Integer.parseInt(payload.get("resourceId").toString());
            Double progressPercentage = Double.parseDouble(
                    payload.getOrDefault("progressPercentage", 0.0).toString());
            Double lastWatchedPosition = Double.parseDouble(
                    payload.getOrDefault("lastWatchedPosition", 0.0).toString());

            if (courseId == null || resourceId == null) {
                return ResponseEntity.badRequest().body("Error: Course ID and Resource ID need!");
            }

            CourseProgress courseProgress = courseProgressService.getProgress(
                    student.getUserId(), courseId, resourceId)
                    .orElse(new CourseProgress(student.getUserId(), courseId, resourceId));

            courseProgress.setProgressPercentage(progressPercentage);
            courseProgress.setLastWatchedPosition(lastWatchedPosition);
            courseProgressService.saveProgress(courseProgress);

            return ResponseEntity.ok("Progress updated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/student/get-progress")
    @ResponseBody
    public CourseProgress getProgress(@RequestParam("courseId") Integer courseId,
            @RequestParam("resourceId") Integer resourceId,
            Authentication authentication) {
        User student = (User) authentication.getPrincipal();
        return courseProgressService.getProgress(student.getUserId(), courseId, resourceId)
                .orElseGet(() -> new CourseProgress(student.getUserId(), courseId, resourceId, 0.0, 0.0));
    }

    @GetMapping("/student/course-syllabus/{courseId}")
    public String showStudentCourseSyllabus(@PathVariable Integer courseId, Model model,
            Authentication authentication) {
        User student = (User) authentication.getPrincipal();
        if (!student.hasRole("STUDENT")) {
            return "redirect:/user/dashboard";
        }

        Course course = courseService.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        List<CourseModule> modules = courseModuleService.getModulesByCourseId(courseId);

        QuizAssignment courseQuizAssignment = null;
        List<QuizAssignment> courseQuizAssignments = quizAssignmentService.getAssignmentByCourse(course);
        if (courseQuizAssignments != null && !courseQuizAssignments.isEmpty()) {
            for (QuizAssignment qa : courseQuizAssignments) {
                if (qa.getModule() == null && qa.getTopic() == null) {
                    courseQuizAssignment = qa;
                    break;
                }
            }
        }

        if (modules != null) {
            for (CourseModule mod : modules) {
                List<CourseTopic> topics = courseTopicService.getTopicsByModule(mod.getId());
                mod.setTopics(topics != null ? topics : new ArrayList<>());
                QuizAssignment modQuiz = quizAssignmentService.getAssignmentByCourseAndModule(course, mod);
                mod.setQuizAssignment(modQuiz);
                if (topics != null) {
                    for (CourseTopic topic : topics) {
                        QuizAssignment topQuiz = quizAssignmentService.getAssignmentByTopic(topic);
                        topic.setQuizAssignment(topQuiz);
                        if (topic.getResources() != null) {
                            topic.getResources().forEach(res -> {
                                courseProgressService.getProgress(
                                        student.getUserId(), courseId, res.getId())
                                        .ifPresent(p -> res.setProgress(p.getProgressPercentage()));
                            });
                        }
                    }
                }
            }
            course.setModules(modules);
        } else {
            course.setModules(new ArrayList<>());
        }

        double overallProgress = courseProgressService.calculateOverallProgress(student.getUserId(), courseId);
        model.addAttribute("overallProgress", overallProgress);
        model.addAttribute("course", course);
        model.addAttribute("courseQuizAssignment", courseQuizAssignment);
        model.addAttribute("courseQuizAssignments", courseQuizAssignments);

        // Use existing service method for single-course lookup
        List<LiveClassSchedule> upcomingLiveClasses =
        	    liveClassScheduleService.getActiveOrUpcomingLiveClassesByCourseId(courseId);
        	model.addAttribute("upcomingLiveClasses", upcomingLiveClasses);

               // model.addAttribute("upcomingLiveClasses", upcomingLiveClasses);
        model.addAttribute("currentTime", LocalDateTime.now());


        return "resource-center/course-syllabus";
    }

    @GetMapping("/admin/course-progress")
    public String showAllProgress(Model model) {
        List<CourseProgress> allProgress = courseProgressService.getAllCourseProgress();
        model.addAttribute("progressList", allProgress);
        return "course_progress_dashboard";
    }
}
