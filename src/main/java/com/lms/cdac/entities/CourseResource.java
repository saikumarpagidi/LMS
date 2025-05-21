package com.lms.cdac.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class CourseResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String fileName;
    private String fileType;
    private String fileUrl;
    private Long fileSize;
    private int sequence;
    


    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    private String uploadedBy;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "course_topic_id", nullable = false) 
    private CourseTopic courseTopic;
    
    private String videoDuration;  // Video duration (in minutes or seconds)
    private String videoResolution;  
    // Course resource progress percentage
    
   

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
    private double progressPercentage;
    // Add this method if not present:
    public void setProgress(double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }
}
