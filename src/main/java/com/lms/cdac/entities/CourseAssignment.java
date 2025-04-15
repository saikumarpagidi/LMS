package com.lms.cdac.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "course_assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseAssignment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// The student (user with STUDENT role) to whom the course is assigned
	@ManyToOne
	@JoinColumn(name = "student_id", nullable = true)
	private User student;

	// The faculty (user with FACULTY role) to whom the course is assigned
	@ManyToOne
	@JoinColumn(name = "faculty_id", nullable = true)
	private User faculty;

	// The assigned course
	@ManyToOne
	@JoinColumn(name = "course_id", nullable = false)
	private Course course;
}
