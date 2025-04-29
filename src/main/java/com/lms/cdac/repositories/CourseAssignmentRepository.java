package com.lms.cdac.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lms.cdac.entities.CourseAssignment;
import com.lms.cdac.entities.Quiz;
import com.lms.cdac.entities.User;
import com.lms.cdac.entities.Course;

import jakarta.transaction.Transactional;

public interface CourseAssignmentRepository extends JpaRepository<CourseAssignment, Long> {
	List<CourseAssignment> findByUser(User user);
	
	// Course assignment methods
	List<CourseAssignment> findByCourse(Course course);

	 @Modifying
	    @Transactional
	    @Query("delete from QuizAssignment qa where qa.quiz = :quiz")
	    void deleteByQuiz(Quiz quiz);

    // Add method to find assignments by faculty
    @Query("SELECT ca FROM CourseAssignment ca WHERE ca.user = :user")
    List<CourseAssignment> findByUserWithRole(@Param("user") User user);
}

