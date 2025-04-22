package com.lms.cdac.services;

import com.lms.cdac.entities.CourseProgress;
import java.util.List;
import java.util.Optional;

public interface CourseProgressService {
    void updateProgress(String studentId, Integer courseId, Integer resourceId, double lastWatchedPosition, double totalDuration);
    Optional<CourseProgress> getProgress(String studentId, Integer courseId, Integer resourceId);
    List<CourseProgress> getAllProgressByStudent(String studentId);
    void saveProgress(CourseProgress courseProgress);
    
    double calculateOverallProgress(String studentId, Integer courseId);
    
    List<CourseProgress> getAllCourseProgress();

}
