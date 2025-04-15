package com.lms.cdac.services;

import java.util.List;
import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.CourseModule;
import com.lms.cdac.entities.Quiz;
import com.lms.cdac.entities.QuizAssignment;
import com.lms.cdac.entities.CourseTopic;

public interface QuizAssignmentService {
	QuizAssignment assignQuizToCourse(Course course, Quiz quiz);

	List<QuizAssignment> getAssignmentByCourse(Course course);

	// New: assign quiz to a specific module
	QuizAssignment assignQuizToModule(Course course, Quiz quiz, CourseModule module);

	// New: fetch assignment for a given course and module
	QuizAssignment getAssignmentByCourseAndModule(Course course, CourseModule module);

	QuizAssignment getAssignmentByModule(CourseModule module);
	
	// New: delete a quiz assignment by its ID
	void deleteAssignment(Long assignmentId);
	
	// New: assign quiz to a specific topic
	QuizAssignment assignQuizToTopic(Course course, Quiz quiz, CourseTopic topic);
	
	// New: fetch assignment for a given course and topic
	QuizAssignment getAssignmentByCourseAndTopic(Course course, CourseTopic topic);
	
	// New: fetch assignment for a given topic
	QuizAssignment getAssignmentByTopic(CourseTopic topic);
}
