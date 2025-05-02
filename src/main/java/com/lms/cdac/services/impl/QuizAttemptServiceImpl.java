package com.lms.cdac.services.impl;

import com.lms.cdac.entities.Quiz;
import com.lms.cdac.entities.QuizAttempt;
import com.lms.cdac.entities.QuizQuestion;
import com.lms.cdac.entities.User;
import com.lms.cdac.entities.Course;
import com.lms.cdac.repositories.QuizAttemptRepository;
import com.lms.cdac.services.QuizAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizAttemptServiceImpl implements QuizAttemptService {

    private final QuizAttemptRepository quizAttemptRepository;

    @Override
    @Transactional
    public QuizAttempt submitAttempt(User student, Quiz quiz, Map<Long, String> answers) {
        // Check if student has already attempted this quiz
        Optional<QuizAttempt> previousAttempt = findPreviousAttempt(student, quiz);
        if (previousAttempt.isPresent()) {
            throw new IllegalStateException("You have already attempted this quiz. Your score was: " 
                + String.format("%.1f%%", previousAttempt.get().getScorePercentage()) 
                + " (" + previousAttempt.get().getCorrectAnswers() 
                + "/" + previousAttempt.get().getTotalQuestions() + " correct)");
        }

        int correctAnswers = 0;
        int totalQuestions = quiz.getQuestions().size();

        for (QuizQuestion question : quiz.getQuestions()) {
            String submitted = answers.get(question.getId());
            if (submitted != null && submitted.trim().equalsIgnoreCase(question.getCorrectAnswer().trim())) {
                correctAnswers++;
            }
        }

        QuizAttempt attempt = QuizAttempt.builder()
                .student(student)
                .quiz(quiz)
                .correctAnswers(correctAnswers)
                .totalQuestions(totalQuestions)
                .attemptDetails(answers.toString())
                .build();
        return quizAttemptRepository.save(attempt);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizAttempt> getQuizAttemptsByStudentAndCourse(User student, Course course) {
        return quizAttemptRepository.findByStudentAndCourse(student, course);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QuizAttempt> findPreviousAttempt(User student, Quiz quiz) {
        return quizAttemptRepository.findByStudentAndQuiz(student, quiz);
    }
}
