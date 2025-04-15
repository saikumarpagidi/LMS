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
@Table(name = "quiz_assignment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAssignment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// The course to which the quiz is assigned
	@ManyToOne
	@JoinColumn(name = "course_id", nullable = false)
	private Course course;

	// The assigned quiz
	@ManyToOne
	@JoinColumn(name = "quiz_id", nullable = false)
	private Quiz quiz;

	// Optional: The module to which the quiz is assigned (if any)
	@ManyToOne
	@JoinColumn(name = "module_id")
	private CourseModule module;
	
	// Optional: The topic to which the quiz is assigned (if any)
	@ManyToOne
	@JoinColumn(name = "topic_id")
	private CourseTopic topic;
}
