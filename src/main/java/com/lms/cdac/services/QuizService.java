package com.lms.cdac.services;

import com.lms.cdac.entities.Quiz;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface QuizService {
    Quiz createQuizFromCSV(MultipartFile file, String title, String description) throws Exception;
    Quiz getQuizById(Long id);
    
    // New method to retrieve all quizzes.
    List<Quiz> getAllQuizzes();
	Quiz saveQuiz(Quiz quiz);
	void deleteQuiz(Long id);
	
}
