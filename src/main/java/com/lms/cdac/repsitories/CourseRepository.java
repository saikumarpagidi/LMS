package com.lms.cdac.repsitories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.cdac.entities.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

}
 