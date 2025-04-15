package com.lms.cdac.services;

import java.util.List;

import com.lms.cdac.entities.CourseModule;

public interface CourseModuleService {
	List<CourseModule> getModulesByCourseId(Integer courseId);

	CourseModule addModule(Integer courseId, String moduleName);

	// New: get a module by its ID
	CourseModule getModuleById(Integer moduleId);

	Integer getCourseIdByModuleId(Integer moduleId);
}
