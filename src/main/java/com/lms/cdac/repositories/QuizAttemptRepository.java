package com.lms.cdac.repositories;

import com.lms.cdac.entities.Quiz;
import com.lms.cdac.entities.QuizAttempt;
import com.lms.cdac.entities.User;
import com.lms.cdac.entities.Course;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    Optional<QuizAttempt> findByStudentAndQuiz(User student, Quiz quiz);
    List<QuizAttempt> findByQuiz(Quiz quiz);
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.student = :student AND qa.quiz IN (SELECT qas.quiz FROM QuizAssignment qas WHERE qas.course = :course)")
    List<QuizAttempt> findByStudentAndCourse(User student, Course course);
    @Modifying
    @Transactional
    @Query("delete from QuizAttempt qa where qa.quiz = :quiz")
    void deleteByQuiz(Quiz quiz);
}
