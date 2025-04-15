package com.lms.cdac.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

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

	@Column(nullable = false, updatable = false)
	private LocalDateTime uploadedAt;

	private String uploadedBy;

	// Ignore the Course reference here because Course already contains modules with
	// topics.
	@ManyToOne
	@JoinColumn(name = "course_id", nullable = false)
	@JsonIgnore
	private Course course;

	// Back reference from resource to topic; prevents recursion.
	@ManyToOne
	@JoinColumn(name = "course_topic_id", nullable = false)
	@JsonBackReference("topic-resources")
	private CourseTopic courseTopic;

	@PrePersist
	protected void onCreate() {
		uploadedAt = LocalDateTime.now();
	}
}
