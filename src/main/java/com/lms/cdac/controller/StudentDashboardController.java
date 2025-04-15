package com.lms.cdac.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.CourseAssignment;
import com.lms.cdac.entities.CourseModule;
import com.lms.cdac.entities.CourseResource;
import com.lms.cdac.entities.CourseSchedule;
import com.lms.cdac.entities.CourseTopic;
import com.lms.cdac.entities.QuizAssignment;
import com.lms.cdac.entities.User;
import com.lms.cdac.services.CourseAssignmentService;
import com.lms.cdac.services.CourseModuleService;
import com.lms.cdac.services.CourseResourceService;
import com.lms.cdac.services.CourseScheduleService;
import com.lms.cdac.services.CourseService;
import com.lms.cdac.services.CourseTopicService;
import com.lms.cdac.services.QuizAssignmentService;

@Controller
public class StudentDashboardController {

	private final CourseAssignmentService courseAssignmentService;
	private final CourseResourceService courseResourceService;
	private final CourseScheduleService courseScheduleService;
	private final CourseService courseService;
	private final CourseModuleService courseModuleService;
	private final CourseTopicService courseTopicService;
	private final QuizAssignmentService quizAssignmentService;

	public StudentDashboardController(CourseAssignmentService courseAssignmentService,
			CourseResourceService courseResourceService, CourseScheduleService courseScheduleService,
			CourseService courseService, CourseModuleService courseModuleService, CourseTopicService courseTopicService,
			QuizAssignmentService quizAssignmentService) {
		this.courseAssignmentService = courseAssignmentService;
		this.courseResourceService = courseResourceService;
		this.courseScheduleService = courseScheduleService;
		this.courseService = courseService;
		this.courseModuleService = courseModuleService;
		this.courseTopicService = courseTopicService;
		this.quizAssignmentService = quizAssignmentService;
	}

	@GetMapping("/student/dashboard")
	public String studentDashboard(Model model, Authentication authentication) {
		User student = (User) authentication.getPrincipal();
		if (!student.hasRole("STUDENT")) {
			return "redirect:/user/dashboard";
		}

		// Fetch course assignments for the student
		List<CourseAssignment> assignments = courseAssignmentService.getAssignmentsForStudent(student);

		// Load resources and schedules for each assigned course
		Map<Integer, CourseSchedule> scheduleMap = new HashMap<>();
		assignments.forEach(assignment -> {
			Integer courseId = assignment.getCourse().getId();
			List<CourseResource> resources = courseResourceService.getResourcesByCourse(courseId);
			assignment.getCourse().setResources(resources);

			List<CourseSchedule> schedules = courseScheduleService.getScheduleByCourseId(courseId);
			if (!schedules.isEmpty()) {
				scheduleMap.put(courseId, schedules.get(0)); // Assuming the first schedule is the primary one
			}
		});

		model.addAttribute("assignments", assignments);
		model.addAttribute("scheduleMap", scheduleMap);
		return "student-dashboard";
	}

	@GetMapping("/student/course-syllabus/{courseId}")
	public String showStudentCourseSyllabus(@PathVariable Integer courseId, Model model,
			Authentication authentication) {
		User student = (User) authentication.getPrincipal();
		if (!student.hasRole("STUDENT")) {
			return "redirect:/user/dashboard";
		}

		// Fetch the course
		Course course = courseService.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));

		// Load modules and their topics for the course
		List<CourseModule> modules = courseModuleService.getModulesByCourseId(courseId);
		
		// Get course-level quiz assignment (where module and topic are null)
		QuizAssignment courseQuizAssignment = null;
		List<QuizAssignment> courseQuizAssignments = quizAssignmentService.getAssignmentByCourse(course);
		if (courseQuizAssignments != null && !courseQuizAssignments.isEmpty()) {
			for (QuizAssignment assignment : courseQuizAssignments) {
				if (assignment.getModule() == null && assignment.getTopic() == null) {
					courseQuizAssignment = assignment;
					break;
				}
			}
		}

		if (modules != null) {
			for (CourseModule module : modules) {
				List<CourseTopic> topics = courseTopicService.getTopicsByModule(module.getId());
				module.setTopics(topics != null ? topics : new ArrayList<>());

				// Get module-level quiz assignment (where topic is null)
				QuizAssignment moduleQuizAssignment = quizAssignmentService.getAssignmentByCourseAndModule(course, module);
				module.setQuizAssignment(moduleQuizAssignment);
				
				// Fetch and set the quiz assignment for each topic
				if (topics != null) {
					for (CourseTopic topic : topics) {
						// Get topic-level quiz assignment
						QuizAssignment topicQuizAssignment = quizAssignmentService.getAssignmentByTopic(topic);
						topic.setQuizAssignment(topicQuizAssignment);
					}
				}
			}
			course.setModules(modules);
		} else {
			course.setModules(new ArrayList<>());
		}
		
		model.addAttribute("course", course);
		model.addAttribute("courseQuizAssignment", courseQuizAssignment);
		model.addAttribute("courseQuizAssignments", courseQuizAssignments);
		return "resource-center/course-syllabus";
	}
}
