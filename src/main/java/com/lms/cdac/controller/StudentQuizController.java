package com.lms.cdac.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.CourseModule;
import com.lms.cdac.entities.CourseTopic;
import com.lms.cdac.entities.Quiz;
import com.lms.cdac.entities.QuizAssignment;
import com.lms.cdac.entities.User;
import com.lms.cdac.services.CourseModuleService;
import com.lms.cdac.services.CourseService;
import com.lms.cdac.services.CourseTopicService;
import com.lms.cdac.services.QuizAssignmentService;
import com.lms.cdac.services.QuizAttemptService;
import com.lms.cdac.services.QuizService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/student/quiz")
public class StudentQuizController {

	private final QuizService quizService;
	private final QuizAttemptService quizAttemptService;
	private final QuizAssignmentService quizAssignmentService;
	private final CourseService courseService;
	private final CourseModuleService courseModuleService; // Injected for module lookup
	private final CourseTopicService courseTopicService; // Injected for topic lookup

	// Display quiz attempt page. Lookup quiz based on the course assignment.
	// Now accepts an optional moduleId and topicId parameters.
	@GetMapping("/{courseId}")
	public String showQuiz(@PathVariable Integer courseId,
			@RequestParam(value = "moduleId", required = false) Integer moduleId,
			@RequestParam(value = "topicId", required = false) Integer topicId, 
			Model model,
			Authentication authentication) {
		try {
			User student = (User) authentication.getPrincipal();
			Course course = courseService.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));

			QuizAssignment assignment = null;
			
			if (topicId != null) {
				CourseTopic topic = courseTopicService.getTopicById(topicId);
				assignment = quizAssignmentService.getAssignmentByTopic(topic);
				
				if (assignment == null) {
					model.addAttribute("error", "No quiz is assigned for this topic.");
					return "student/quiz-error";
				}
			} else if (moduleId != null) {
				CourseModule module = courseModuleService.getModuleById(moduleId);
				assignment = quizAssignmentService.getAssignmentByCourseAndModule(course, module);
				
				if (assignment == null) {
					model.addAttribute("error", "No quiz is assigned for this module.");
					return "student/quiz-error";
				}
			} else {
				var assignments = quizAssignmentService.getAssignmentByCourse(course);
				if (assignments != null && !assignments.isEmpty()) {
					for (QuizAssignment quizAssignment : assignments) {
						if (quizAssignment.getModule() == null && quizAssignment.getTopic() == null) {
							assignment = quizAssignment;
							break;
						}
					}
				}
				
				if (assignment == null) {
					model.addAttribute("error", "No quiz is assigned for this course.");
					return "student/quiz-error";
				}
			}

			Quiz quiz = assignment.getQuiz();
			if (quiz == null) {
				model.addAttribute("error", "The assigned quiz could not be found.");
				return "student/quiz-error";
			}

			// Check if student has already attempted this quiz
			var previousAttempt = quizAttemptService.findPreviousAttempt(student, quiz);
			if (previousAttempt.isPresent()) {
				model.addAttribute("attempt", previousAttempt.get());
				model.addAttribute("message", String.format("You have already attempted this quiz. Your score was: %.1f%% (%d/%d correct)", 
					previousAttempt.get().getScorePercentage(),
					previousAttempt.get().getCorrectAnswers(),
					previousAttempt.get().getTotalQuestions()));
				return "student/quiz-result";
			}

			model.addAttribute("quiz", quiz);
			return "student/quiz-attempt";
		} catch (Exception e) {
			model.addAttribute("error", "An error occurred: " + e.getMessage());
			return "student/quiz-error";
		}
	}

	// Process quiz submission from student.
	@PostMapping("/{quizId}/submit")
	public String submitQuiz(@PathVariable Long quizId, @RequestParam Map<String, String> allParams,
			Authentication authentication, Model model) {
		try {
			User student = (User) authentication.getPrincipal();
			Quiz quiz = quizService.getQuizById(quizId);
			if (quiz == null) {
				model.addAttribute("error", "Quiz not found.");
				return "student/quiz-error";
			}

			Map<Long, String> answers = allParams.entrySet().stream()
					.filter(e -> e.getKey().startsWith("q_"))
					.collect(java.util.stream.Collectors.toMap(
							e -> Long.parseLong(e.getKey().substring(2)),
							Map.Entry::getValue));

			var attempt = quizAttemptService.submitAttempt(student, quiz, answers);
			model.addAttribute("attempt", attempt);
			return "student/quiz-result";
		} catch (IllegalStateException e) {
			model.addAttribute("error", e.getMessage());
			return "student/quiz-error";
		} catch (Exception e) {
			model.addAttribute("error", "An error occurred while submitting the quiz: " + e.getMessage());
			return "student/quiz-error";
		}
	}
}
