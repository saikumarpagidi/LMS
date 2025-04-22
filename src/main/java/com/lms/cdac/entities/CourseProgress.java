package com.lms.cdac.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CourseProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentId;
    private Integer courseId;
    private Integer resourceId;
    private double progressPercentage;
    private double lastWatchedPosition;

    
    public CourseProgress(String studentId, Integer courseId, Integer resourceId) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.resourceId = resourceId;
        this.progressPercentage = 0.0;
        this.lastWatchedPosition = 0.0;
    }

    
    public CourseProgress(String studentId, Integer courseId, Integer resourceId, double progressPercentage, double lastWatchedPosition) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.resourceId = resourceId;
        this.progressPercentage = progressPercentage;
        this.lastWatchedPosition = lastWatchedPosition;
    }
}
