package com.lms.cdac.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.CourseModule;
import com.lms.cdac.repsitories.CourseModuleRepository;
import com.lms.cdac.repsitories.CourseRepository;
import com.lms.cdac.services.CourseModuleService;

@Service
public class CourseModuleServiceImpl implements CourseModuleService {

	private final CourseModuleRepository courseModuleRepository;
	private final CourseRepository courseRepository;

	public CourseModuleServiceImpl(CourseModuleRepository courseModuleRepository, CourseRepository courseRepository) {
		this.courseModuleRepository = courseModuleRepository;
		this.courseRepository = courseRepository;
	}

	@Override
	public List<CourseModule> getModulesByCourseId(Integer courseId) {
		return courseModuleRepository.findByCourseId(courseId);
	}

	@Override
	public CourseModule addModule(Integer courseId, String moduleName) {
		Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
		CourseModule module = new CourseModule();
		module.setModuleName(moduleName);
		module.setCourse(course);
		return courseModuleRepository.save(module);
	}

	@Override
	public Integer getCourseIdByModuleId(Integer moduleId) {
		return courseModuleRepository.findCourseIdByModuleId(moduleId)
				.orElseThrow(() -> new IllegalArgumentException("Module not found with ID: " + moduleId));
	}

	// New method: Retrieve a module by its ID
	@Override
	public CourseModule getModuleById(Integer moduleId) {
		return courseModuleRepository.findById(moduleId)
				.orElseThrow(() -> new IllegalArgumentException("Module not found with ID: " + moduleId));
	}
}
