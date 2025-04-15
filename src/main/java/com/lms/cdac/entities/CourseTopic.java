package com.lms.cdac.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Transient;

@Entity
@Getter
@Setter
public class CourseTopic {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String topicName;

	// Back reference to module; prevents recursing from topic back into module.
	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "module_id", nullable = false)
	@JsonBackReference("module-topics")
	private CourseModule courseModule;

	// Although a topic is linked to a course, we ignore it to avoid duplicate
	// serialization.
	@ManyToOne
	@JoinColumn(name = "course_id", nullable = false)
	@JsonIgnore
	private Course course;

	// Forward relationship to resources; include resources for the topic.
	@OneToMany(mappedBy = "courseTopic", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonManagedReference("topic-resources")
	private List<CourseResource> resources = new ArrayList<>();
	
	// Transient property to hold a topic-level quiz assignment (populated in the controller)
	@Transient
	private QuizAssignment quizAssignment;

	public void setResources(List<CourseResource> resources) {
		this.resources.clear();
		if (resources != null) {
			this.resources.addAll(resources);
		}
	}
}
