package com.lms.cdac.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CourseModule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String moduleName;

	// The back reference to Course breaks the cycle when serializing modules inside
	// a course
	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "course_id", nullable = false)
	@JsonBackReference("course-modules")
	private Course course;

	// Use JsonManagedReference for topics so that their back reference doesn't
	// cause recursion.
	@OneToMany(mappedBy = "courseModule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonManagedReference("module-topics")
	private List<CourseTopic> topics = new ArrayList<>();

	// Transient property to hold a module-level quiz assignment (populated in the
	// controller)
	@Transient
	private QuizAssignment quizAssignment;

	public void setTopics(List<CourseTopic> topics) {
		this.topics.clear();
		if (topics != null) {
			this.topics.addAll(topics);
		}
	}
}
