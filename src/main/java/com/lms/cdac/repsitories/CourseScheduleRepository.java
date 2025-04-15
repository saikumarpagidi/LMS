package com.lms.cdac.repsitories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.cdac.entities.CourseSchedule;

@Repository
public interface CourseScheduleRepository extends JpaRepository<CourseSchedule, Integer> {
	List<CourseSchedule> findByResourceCenterName(String resourceCenterName);

	Optional<CourseSchedule> findById(Integer courseId);

	List<CourseSchedule> findByCourse_Id(Integer courseId);
	List<CourseSchedule> findByResourceCenterNameIgnoreCase(String resourceCenterName);

}
