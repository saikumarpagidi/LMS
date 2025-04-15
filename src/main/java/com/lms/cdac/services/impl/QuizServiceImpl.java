package com.lms.cdac.services.impl;

import com.lms.cdac.entities.Quiz;
import com.lms.cdac.entities.QuizAssignment;
import com.lms.cdac.entities.QuizAttempt;
import com.lms.cdac.entities.QuizQuestion;
import com.lms.cdac.entities.QuizOption;
import com.lms.cdac.repositories.QuizRepository;
import com.lms.cdac.repositories.QuizAssignmentRepository;
import com.lms.cdac.repositories.QuizAttemptRepository;
import com.lms.cdac.services.QuizService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    // Inject QuizAssignmentRepository to manually delete assignments
    private final QuizAssignmentRepository quizAssignmentRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    @Override
    public Quiz saveQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }
    
    @Override
    public Quiz createQuizFromCSV(MultipartFile file, String title, String description) throws Exception {
        Quiz quiz = Quiz.builder().title(title).description(description).build();
        // Expected headers: Question,OptionA,OptionB,OptionC,OptionD,CorrectAnswer,QuestionType
        Reader in = new InputStreamReader(file.getInputStream());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().parse(in);
        for (CSVRecord record : records) {
            String questionText = record.get("Question");
            String questionType = record.get("QuestionType");
            String correctAnswer = record.get("CorrectAnswer");

            // Create question and add to quiz's questions list (initialized via @Builder.Default)
            var question = com.lms.cdac.entities.QuizQuestion.builder()
                    .questionText(questionText)
                    .questionType(questionType)
                    .correctAnswer(correctAnswer)
                    .quiz(quiz)
                    .build();
            quiz.getQuestions().add(question);

            // If MCQ, create QuizOption objects for options A-D
            if ("MCQ".equalsIgnoreCase(questionType)) {
                String optionA = record.get("OptionA");
                if (optionA != null && !optionA.isEmpty()) {
                    boolean isCorrect = "A".equalsIgnoreCase(correctAnswer);
                    var opt = com.lms.cdac.entities.QuizOption.builder()
                            .optionLetter("A")
                            .optionText(optionA.trim())
                            .isCorrect(isCorrect)
                            .question(question)
                            .build();
                    question.getOptions().add(opt);
                }
                String optionB = record.get("OptionB");
                if (optionB != null && !optionB.isEmpty()) {
                    boolean isCorrect = "B".equalsIgnoreCase(correctAnswer);
                    var opt = com.lms.cdac.entities.QuizOption.builder()
                            .optionLetter("B")
                            .optionText(optionB.trim())
                            .isCorrect(isCorrect)
                            .question(question)
                            .build();
                    question.getOptions().add(opt);
                }
                String optionC = record.get("OptionC");
                if (optionC != null && !optionC.isEmpty()) {
                    boolean isCorrect = "C".equalsIgnoreCase(correctAnswer);
                    var opt = com.lms.cdac.entities.QuizOption.builder()
                            .optionLetter("C")
                            .optionText(optionC.trim())
                            .isCorrect(isCorrect)
                            .question(question)
                            .build();
                    question.getOptions().add(opt);
                }
                String optionD = record.get("OptionD");
                if (optionD != null && !optionD.isEmpty()) {
                    boolean isCorrect = "D".equalsIgnoreCase(correctAnswer);
                    var opt = com.lms.cdac.entities.QuizOption.builder()
                            .optionLetter("D")
                            .optionText(optionD.trim())
                            .isCorrect(isCorrect)
                            .question(question)
                            .build();
                    question.getOptions().add(opt);
                }
            }
        }
        return quizRepository.save(quiz);
    }

    @Override
    public Quiz getQuizById(Long id) {
        return quizRepository.findById(id).orElse(null);
    }

    @Override
    public List<Quiz> getAllQuizzes() {
        List<Quiz> quizzes = quizRepository.findAll();
        System.out.println("DEBUG: Found quizzes: " + quizzes);
        return quizzes;
    }

    @Override
    public void deleteQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id).orElse(null);
        if (quiz != null) {
            // Bulk delete quiz attempts referencing this quiz
            quizAttemptRepository.deleteByQuiz(quiz);

            // Bulk delete quiz assignments referencing this quiz
            quizAssignmentRepository.deleteByQuiz(quiz);

            // Finally, delete the quiz itself
            quizRepository.delete(quiz);
        }
    }



}
