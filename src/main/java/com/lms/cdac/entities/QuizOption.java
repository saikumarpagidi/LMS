package com.lms.cdac.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quiz_option")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // New field to store option letter (e.g., "A", "B", "C", or "D")
    private String optionLetter;
    
    private String optionText;
    private boolean isCorrect;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion question;
}
