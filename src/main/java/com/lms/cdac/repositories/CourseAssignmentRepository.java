package com.lms.cdac.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.lms.cdac.entities.CourseAssignment;
import com.lms.cdac.entities.Quiz;
import com.lms.cdac.entities.User;

import jakarta.transaction.Transactional;

public interface CourseAssignmentRepository extends JpaRepository<CourseAssignment, Long> {
	List<CourseAssignment> findByStudent(User student);
	
	// Faculty assignment methods
	List<CourseAssignment> findByFaculty(User faculty);

	 @Modifying
	    @Transactional
	    @Query("delete from QuizAssignment qa where qa.quiz = :quiz")
	    void deleteByQuiz(Quiz quiz);
}

