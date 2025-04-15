package com.lms.cdac.repsitories;

import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.LabAttendance;
import com.lms.cdac.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabAttendanceRepository extends JpaRepository<LabAttendance, Integer> {
    
    List<LabAttendance> findByStudent(User student);
    
    List<LabAttendance> findByCourse(Course course);
    
    List<LabAttendance> findByStudentAndCourse(User student, Course course);
    
    List<LabAttendance> findByFaculty(User faculty);
    
    @Query("SELECT la FROM LabAttendance la WHERE la.uniquePracticalKey = :key")
    List<LabAttendance> findByUniquePracticalKey(@Param("key") String key);
    
    @Query("SELECT la FROM LabAttendance la WHERE la.course.id = :courseId AND la.student.id = :studentId")
    List<LabAttendance> findByCourseIdAndStudentId(@Param("courseId") Integer courseId, @Param("studentId") String studentId);
} 