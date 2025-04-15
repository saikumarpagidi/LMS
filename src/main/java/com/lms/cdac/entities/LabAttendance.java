package com.lms.cdac.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "lab_attendance")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LabAttendance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @NotEmpty(message = "Practical name cannot be empty")
    private String practicalName;
    
    @NotNull(message = "Attendance status is required")
    private boolean present;
    
    @Column(name = "attendance_status")
    private String attendanceStatus;
    
    @NotNull(message = "Marks are required")
    private Integer marks;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id", nullable = false)
    private User faculty;
    
    @Column(name = "upload_date")
    private LocalDateTime uploadDate;
    
    @Column(name = "unique_practical_key")
    private String uniquePracticalKey; // For checking duplicates
    
    @NotEmpty(message = "Course name cannot be empty")
    @Column(name = "course_name", nullable = false)
    private String courseName;
    
    @NotEmpty(message = "Student name cannot be empty")
    @Column(name = "student_name", nullable = false)
    private String studentName;
} 