package com.lms.cdac.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lms.cdac.entities.CourseSchedule;
import com.lms.cdac.repsitories.CourseScheduleRepository;
import com.lms.cdac.services.CourseScheduleService;

@Service
public class CourseScheduleServiceImpl implements CourseScheduleService {

	@Autowired
	private CourseScheduleRepository courseScheduleRepository;

	@Override
	public List<CourseSchedule> getAllCourseSchedules() {
		return courseScheduleRepository.findAll();
	}

	@Override
	public CourseSchedule getCourseScheduleById(Integer id) {
		return courseScheduleRepository.findById(id).orElse(null);
	}

	@Override
	public void createCourseSchedule(CourseSchedule courseSchedule) {
		courseScheduleRepository.save(courseSchedule);
	}

	@Override
	public void updateCourseSchedule(CourseSchedule courseSchedule) {
		courseScheduleRepository.save(courseSchedule);
	}

	@Override
	public void deleteCourseSchedule(Integer id) {
		courseScheduleRepository.deleteById(id);
	}

	@Override
	public List<CourseSchedule> getScheduledCoursesByResourceCenter(String resourceCenterName) {
		return courseScheduleRepository.findByResourceCenterName(resourceCenterName);
	}

	
	@Override
	public List<CourseSchedule> getScheduleByCourseId(Integer courseId) {
		return courseScheduleRepository.findByCourse_Id(courseId);
	}
}
