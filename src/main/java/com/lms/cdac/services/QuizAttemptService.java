package com.lms.cdac.services;

import com.lms.cdac.entities.Quiz;
import com.lms.cdac.entities.QuizAttempt;
import com.lms.cdac.entities.User;
import java.util.Map;

public interface QuizAttemptService {
    QuizAttempt submitAttempt(User student, Quiz quiz, Map<Long, String> answers);
}
