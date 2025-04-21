package com.lms.cdac.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "class_attendance")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClassAttendance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @NotNull(message = "Day number is required")
    @Column(name = "day_number")
    private Integer dayNumber;
    
    @NotNull(message = "Attendance status is required")
    private boolean present;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id", nullable = false)
    private User faculty;
    
    @Column(name = "attendance_date")
    private LocalDateTime attendanceDate;
    
    @Column(name = "course_name", nullable = false)
    private String courseName;
    
    @Column(name = "student_name", nullable = false)
    private String studentName;
} 