package com.lms.cdac.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.CourseModule;
import com.lms.cdac.entities.CourseTopic;
import com.lms.cdac.entities.Quiz;
import com.lms.cdac.entities.QuizAssignment;

@Repository
public interface QuizAssignmentRepository extends JpaRepository<QuizAssignment, Long> {
	List<QuizAssignment> findByCourse(Course course);

	List<QuizAssignment> findByQuiz(Quiz quiz);

	void deleteByQuiz(Quiz quiz);

	// New method to get an assignment by course and module
	Optional<QuizAssignment> findByCourseAndModule(Course course, CourseModule module);

	Optional<QuizAssignment> findByModule(CourseModule module);
	
	// New methods for topic-level assignments
	Optional<QuizAssignment> findByCourseAndTopic(Course course, CourseTopic topic);
	
	Optional<QuizAssignment> findByTopic(CourseTopic topic);
}
