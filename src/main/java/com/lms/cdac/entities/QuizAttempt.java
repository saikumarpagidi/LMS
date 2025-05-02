package com.lms.cdac.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quiz_attempt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The student who attempted the quiz
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    // The quiz attempted
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    // Number of correct answers
    private int correctAnswers;
    
    // Total number of questions
    private int totalQuestions;
    
    // Store attempt details as a regular string instead of CLOB
    @Column(columnDefinition = "TEXT")
    private String attemptDetails;
    
    // Calculate percentage score
    @Transient
    public double getScorePercentage() {
        if (totalQuestions == 0) return 0.0;
        return ((double) correctAnswers / totalQuestions) * 100;
    }
}
