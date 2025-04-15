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
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    // The quiz attempted
    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    // Calculated score for the attempt
    private int score;
    
    // Optionally, you can store attempt details (e.g. answers in JSON format)
    @Lob
    private String attemptDetails;
}
