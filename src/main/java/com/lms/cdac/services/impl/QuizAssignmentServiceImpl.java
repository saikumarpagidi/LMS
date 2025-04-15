package com.lms.cdac.services.impl;

import java.util.List;
import org.springframework.stereotype.Service;

import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.CourseModule;
import com.lms.cdac.entities.CourseTopic;
import com.lms.cdac.entities.Quiz;
import com.lms.cdac.entities.QuizAssignment;
import com.lms.cdac.repositories.QuizAssignmentRepository;
import com.lms.cdac.services.QuizAssignmentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizAssignmentServiceImpl implements QuizAssignmentService {

	private final QuizAssignmentRepository quizAssignmentRepository;

	@Override
	public QuizAssignment assignQuizToCourse(Course course, Quiz quiz) {
		QuizAssignment assignment = QuizAssignment.builder().course(course).quiz(quiz).build();
		return quizAssignmentRepository.save(assignment);
	}

	@Override
	public List<QuizAssignment> getAssignmentByCourse(Course course) {
		return quizAssignmentRepository.findByCourse(course);
	}

	@Override
	public QuizAssignment assignQuizToModule(Course course, Quiz quiz, CourseModule module) {
		QuizAssignment assignment = QuizAssignment.builder().course(course).quiz(quiz).module(module).build();
		return quizAssignmentRepository.save(assignment);
	}

	@Override
	public QuizAssignment getAssignmentByCourseAndModule(Course course, CourseModule module) {
		return quizAssignmentRepository.findByCourseAndModule(course, module).orElse(null);
	}

	@Override
	public QuizAssignment getAssignmentByModule(CourseModule module) {
		return quizAssignmentRepository.findByModule(module).orElse(null);
	}

	@Override
	public void deleteAssignment(Long assignmentId) {
		quizAssignmentRepository.deleteById(assignmentId);
	}

	@Override
	public QuizAssignment assignQuizToTopic(Course course, Quiz quiz, CourseTopic topic) {
		QuizAssignment assignment = QuizAssignment.builder()
				.course(course)
				.quiz(quiz)
				.topic(topic)
				.build();
		return quizAssignmentRepository.save(assignment);
	}

	@Override
	public QuizAssignment getAssignmentByCourseAndTopic(Course course, CourseTopic topic) {
		return quizAssignmentRepository.findByCourseAndTopic(course, topic).orElse(null);
	}

	@Override
	public QuizAssignment getAssignmentByTopic(CourseTopic topic) {
		return quizAssignmentRepository.findByTopic(topic).orElse(null);
	}

}
