package com.lms.cdac.repositories;

import com.lms.cdac.entities.QuizAttempt;
import com.lms.cdac.entities.Quiz;
import com.lms.cdac.entities.User;

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
    @Modifying
    @Transactional
    @Query("delete from QuizAttempt qa where qa.quiz = :quiz")
    void deleteByQuiz(Quiz quiz);
}
