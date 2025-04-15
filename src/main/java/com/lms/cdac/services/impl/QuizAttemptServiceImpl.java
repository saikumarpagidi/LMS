package com.lms.cdac.services.impl;

import com.lms.cdac.entities.Quiz;
import com.lms.cdac.entities.QuizAttempt;
import com.lms.cdac.entities.QuizQuestion;
import com.lms.cdac.entities.User;
import com.lms.cdac.repositories.QuizAttemptRepository;
import com.lms.cdac.services.QuizAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuizAttemptServiceImpl implements QuizAttemptService {

    private final QuizAttemptRepository quizAttemptRepository;

    @Override
    public QuizAttempt submitAttempt(User student, Quiz quiz, Map<Long, String> answers) {
        int score = 0;
        for (QuizQuestion question : quiz.getQuestions()) {
            String submitted = answers.get(question.getId());
            if (submitted != null && submitted.trim().equalsIgnoreCase(question.getCorrectAnswer().trim())) {
                score++;
            }
        }
        QuizAttempt attempt = QuizAttempt.builder()
                .student(student)
                .quiz(quiz)
                .score(score)
                .attemptDetails(answers.toString())
                .build();
        return quizAttemptRepository.save(attempt);
    }

}
