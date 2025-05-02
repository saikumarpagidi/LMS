package com.lms.cdac.services;

import com.lms.cdac.entities.Quiz;
import com.lms.cdac.entities.QuizAttempt;
import com.lms.cdac.entities.User;
import com.lms.cdac.entities.Course;
import java.util.Map;
import java.util.List;
import java.util.Optional;

public interface QuizAttemptService {
    QuizAttempt submitAttempt(User student, Quiz quiz, Map<Long, String> answers);
    List<QuizAttempt> getQuizAttemptsByStudentAndCourse(User student, Course course);
    Optional<QuizAttempt> findPreviousAttempt(User student, Quiz quiz);
}
