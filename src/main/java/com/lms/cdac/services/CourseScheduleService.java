package com.lms.cdac.services;

import java.util.List;

import com.lms.cdac.entities.CourseSchedule;

public interface CourseScheduleService {
	List<CourseSchedule> getAllCourseSchedules();

	CourseSchedule getCourseScheduleById(Integer id);

	void createCourseSchedule(CourseSchedule courseSchedule);

	void updateCourseSchedule(CourseSchedule courseSchedule);

	void deleteCourseSchedule(Integer id);

	List<CourseSchedule> getScheduledCoursesByResourceCenter(String resourceCenterName);

	// Optional<CourseSchedule> findById(Long courseId);
	// List<CourseSchedule> getCoursesByResourceCenter(String resourceCenter);
	List<CourseSchedule> getScheduleByCourseId(Integer id);

}
