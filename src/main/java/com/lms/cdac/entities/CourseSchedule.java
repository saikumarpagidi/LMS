package com.lms.cdac.entities;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Transient;

@Entity
public class CourseSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id", nullable = false)
    private Course course;  // Linking to the Course entity
    private String duration;
    private LocalDate startDate;
    private LocalDate endDate;

    // Resource center name, can be expanded to various centers like PMU, AIIMS
    private String resourceCenterName; 
    
   
    
    // Location for the resource center (e.g., PMU Noida, AIIMS Delhi)
    private String location;

    // PMU ID is fetched from the database, not editable in the form
    @Transient
    private Integer pmuId;
                   

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getResourceCenterName() {
        return resourceCenterName;
    }

    public void setResourceCenterName(String resourceCenterName) {
        this.resourceCenterName = resourceCenterName;
    }

    public Integer getPmuId() {
        return pmuId;
    }

    public void setPmuId(Integer pmuId) {
        this.pmuId = pmuId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // Logic to calculate end date based on start date and duration
    @PrePersist
    @PreUpdate
    public void calculateEndDate() {
        if (startDate != null && course != null && course.getDuration() > 0) {
            this.endDate = startDate.plusDays(course.getDuration());  // Use course duration here
        }
    }

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}
}
