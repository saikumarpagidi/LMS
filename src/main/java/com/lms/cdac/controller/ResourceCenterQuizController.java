package com.lms.cdac.controller;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.CourseModule;
import com.lms.cdac.entities.CourseSchedule;
import com.lms.cdac.entities.CourseTopic;
import com.lms.cdac.entities.Quiz;
import com.lms.cdac.entities.QuizAssignment;
import com.lms.cdac.entities.User;
import com.lms.cdac.dto.ModuleDTO;
import com.lms.cdac.dto.TopicDTO;
import com.lms.cdac.services.CourseModuleService;
import com.lms.cdac.services.CourseScheduleService;
import com.lms.cdac.services.CourseService;
import com.lms.cdac.services.CourseTopicService;
import com.lms.cdac.services.QuizAssignmentService;
import com.lms.cdac.services.QuizService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/center/quiz")
public class ResourceCenterQuizController {

	private static final Logger logger = LoggerFactory.getLogger(ResourceCenterQuizController.class);

	private final QuizService quizService;
	private final CourseService courseService;
	private final QuizAssignmentService quizAssignmentService;
	private final CourseModuleService courseModuleService; // Inject the service for modules
	private final CourseScheduleService courseScheduleService; // Inject the service for course schedules
	private final CourseTopicService courseTopicService; // Inject the service for course topics

	// Show quiz upload page
	@GetMapping("/upload")
	public String showUploadQuizPage() {
		return "resource-center/upload-quiz";
	}

	// Process quiz upload (CSV file)
	@PostMapping("/upload")
	public String uploadQuiz(@RequestParam("title") String title, @RequestParam("description") String description,
			@RequestParam("file") MultipartFile file, Model model) {
		try {
			Quiz quiz = quizService.createQuizFromCSV(file, title, description);
			model.addAttribute("message", "Quiz uploaded successfully with id: " + quiz.getId());
		} catch (Exception e) {
			logger.error("Error uploading quiz: {}", e.getMessage());
			model.addAttribute("error", "Quiz upload failed: " + e.getMessage());
		}
		return "resource-center/upload-quiz";
	}

	// View quizzes page
	@GetMapping("/view")
	public String viewQuizzes(Model model) {
		List<Quiz> quizzes = quizService.getAllQuizzes();
		model.addAttribute("quizzes", quizzes);
		return "resource-center/view-quizzes";
	}

	// Delete quiz by id
	@PostMapping("/delete")
	public String deleteQuiz(@RequestParam("quizId") Long quizId, RedirectAttributes redirectAttributes) {
		try {
			quizService.deleteQuiz(quizId);
			redirectAttributes.addFlashAttribute("message", "Quiz deleted successfully");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Failed to delete quiz: " + e.getMessage());
		}
		return "redirect:/center/quiz/view";
	}

	// Show assign quiz page
	@GetMapping("/assign")
	public String showAssignQuizPage(Model model, Authentication authentication) {
		// Get the resource center from the authenticated user
		User user = (User) authentication.getPrincipal();
		String resourceCenter = user.getResourceCenter();
		
		// Get courses scheduled for this resource center
		List<CourseSchedule> courseSchedules = courseScheduleService.getScheduledCoursesByResourceCenter(resourceCenter);
		
		// Extract unique courses from schedules
		List<Course> availableCourses = courseSchedules.stream()
				.map(CourseSchedule::getCourse)
				.distinct()
				.collect(Collectors.toList());
		
		// Get all quizzes
		List<Quiz> quizzes = quizService.getAllQuizzes();
		
		// Add to model
		model.addAttribute("courses", availableCourses);
		model.addAttribute("quizzes", quizzes);
		model.addAttribute("resourceCenter", resourceCenter);
		
		return "resource-center/assign-quiz";
	}

	// New: Endpoint to fetch modules for a selected course (returns JSON)
	@GetMapping("/modules")
	@ResponseBody
	public List<ModuleDTO> getModules(@RequestParam Integer courseId) {
		Course course = courseService.getCourseById(courseId);
		if (course != null) {
			return course.getModules().stream()
				.map(module -> new ModuleDTO(module.getId(), module.getModuleName()))
				.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	// New: Endpoint to fetch topics for a selected module (returns JSON)
	@GetMapping("/topics")
	@ResponseBody
	public List<CourseTopic> getTopics(@RequestParam("moduleId") Integer moduleId) {
		return courseTopicService.getTopicsByModule(moduleId);
	}

	// Process assign quiz form
	@PostMapping("/assign")
	public String assignQuiz(@RequestParam("courseId") Integer courseId, @RequestParam("quizId") Long quizId,
			// Optional moduleId parameter (if provided, assign at module level)
			@RequestParam(value = "moduleId", required = false) Integer moduleId,
			// Optional topicId parameter (if provided, assign at topic level)
			@RequestParam(value = "topicId", required = false) Integer topicId, 
			RedirectAttributes redirectAttributes) {
		try {
			Course course = courseService.findById(courseId)
					.orElseThrow(() -> new RuntimeException("Course not found"));
			Quiz quiz = quizService.getQuizById(quizId);
			QuizAssignment assignment;
			
			if (topicId != null) {
				// Assign quiz to a specific topic
				CourseTopic topic = courseTopicService.getTopicById(topicId);
				
				// Check if a quiz is already assigned to this topic
				QuizAssignment existingAssignment = quizAssignmentService.getAssignmentByTopic(topic);
				if (existingAssignment != null) {
					redirectAttributes.addFlashAttribute("error", 
						"A quiz is already assigned to topic: " + topic.getTopicName() + 
						". Please delete the existing assignment first.");
					return "redirect:/center/quiz/assign";
				}
				
				assignment = quizAssignmentService.assignQuizToTopic(course, quiz, topic);
				redirectAttributes.addFlashAttribute("message", "Quiz assigned successfully to topic: " + topic.getTopicName());
			} else if (moduleId != null) {
				// Assign quiz to a specific module
				CourseModule module = courseModuleService.getModuleById(moduleId);
				
				// Check if a quiz is already assigned to this module
				QuizAssignment existingAssignment = quizAssignmentService.getAssignmentByModule(module);
				if (existingAssignment != null) {
					redirectAttributes.addFlashAttribute("error", 
						"A quiz is already assigned to module: " + module.getModuleName() + 
						". Please delete the existing assignment first.");
					return "redirect:/center/quiz/assign";
				}
				
				assignment = quizAssignmentService.assignQuizToModule(course, quiz, module);
				redirectAttributes.addFlashAttribute("message", "Quiz assigned successfully to module: " + module.getModuleName());
			} else {
				// Assign quiz to the course
				// Check if a quiz is already assigned to this course
				List<QuizAssignment> existingAssignments = quizAssignmentService.getAssignmentByCourse(course);
				if (existingAssignments != null && !existingAssignments.isEmpty()) {
					// Check if there's a course-level assignment (no module or topic)
					boolean hasCourseLevelAssignment = existingAssignments.stream()
						.anyMatch(a -> a.getModule() == null && a.getTopic() == null);
					
					if (hasCourseLevelAssignment) {
						redirectAttributes.addFlashAttribute("error", 
							"A quiz is already assigned to course: " + course.getCourseName() + 
							". Please delete the existing assignment first.");
						return "redirect:/center/quiz/assign";
					}
				}
				
				assignment = quizAssignmentService.assignQuizToCourse(course, quiz);
				redirectAttributes.addFlashAttribute("message", "Quiz assigned successfully to course: " + course.getCourseName());
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Quiz assignment failed: " + e.getMessage());
		}
		return "redirect:/center/quiz/assign";
	}
	
	// New: Show assigned quizzes for the resource center
	@GetMapping("/assigned")
	public String showAssignedQuizzes(Model model, Authentication authentication) {
		// Get the resource center from the authenticated user
		User user = (User) authentication.getPrincipal();
		String resourceCenter = user.getResourceCenter();
		
		// Get courses scheduled for this resource center
		List<CourseSchedule> courseSchedules = courseScheduleService.getScheduledCoursesByResourceCenter(resourceCenter);
		
		// Extract unique courses from schedules
		List<Course> availableCourses = courseSchedules.stream()
				.map(CourseSchedule::getCourse)
				.distinct()
				.collect(Collectors.toList());
		
		// Get all quiz assignments for these courses
		List<QuizAssignment> allAssignments = availableCourses.stream()
				.flatMap(course -> quizAssignmentService.getAssignmentByCourse(course).stream())
				.collect(Collectors.toList());
		
		// Add to model
		model.addAttribute("assignments", allAssignments);
		model.addAttribute("resourceCenter", resourceCenter);
		
		return "resource-center/assigned-quizzes";
	}
	
	// New: Delete a quiz assignment
	@PostMapping("/delete-assignment")
	public String deleteQuizAssignment(@RequestParam("assignmentId") Long assignmentId, RedirectAttributes redirectAttributes) {
		try {
			quizAssignmentService.deleteAssignment(assignmentId);
			redirectAttributes.addFlashAttribute("message", "Quiz assignment deleted successfully");
		} catch (Exception e) {
			logger.error("Error deleting quiz assignment: {}", e.getMessage());
			redirectAttributes.addFlashAttribute("error", "Failed to delete quiz assignment: " + e.getMessage());
		}
		return "redirect:/center/quiz/assigned";
	}
}
