package com.lms.cdac.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lms.cdac.entities.CourseProgress;
import com.lms.cdac.repsitories.CourseProgressRepository;
import com.lms.cdac.services.CourseProgressService;

@Service
public class CourseProgressServiceImpl implements CourseProgressService {

    @Autowired
    private CourseProgressRepository courseProgressRepository;

    @Override
    public void updateProgress(String studentId, Integer courseId, Integer resourceId, double lastWatchedPosition, double totalDuration) {
        if (totalDuration <= 0) {
            throw new IllegalArgumentException("Total duration must be greater than zero.");
        }

        if (lastWatchedPosition < 0) {
            throw new IllegalArgumentException("Last watched position cannot be negative.");
        }

        CourseProgress progress = courseProgressRepository
                .findByStudentIdAndCourseIdAndResourceId(studentId, courseId, resourceId)
                .orElse(new CourseProgress(studentId, courseId, resourceId));

        // Calculate the new progress percentage (capped at 100%)
        double computedProgress = Math.min((lastWatchedPosition / totalDuration) * 100, 100);
        
        // Ensure that once progress is complete (100%), it is not overwritten with a lower value.
        double finalProgress = Math.max(progress.getProgressPercentage(), computedProgress);

        // Optionally, you can update the last watched position too.
        // Yahan hum maximum last watched position store kar rahe hain, taaki complete hone ke baad wo regress na ho.
        double finalLastWatchedPosition = Math.max(progress.getLastWatchedPosition(), lastWatchedPosition);

        progress.setLastWatchedPosition(finalLastWatchedPosition);
        progress.setProgressPercentage(finalProgress);

        // Direct saveProgress() call for validation and saving.
        saveProgress(progress);
    }

    @Override
    public Optional<CourseProgress> getProgress(String studentId, Integer courseId, Integer resourceId) {
        return courseProgressRepository.findByStudentIdAndCourseIdAndResourceId(studentId, courseId, resourceId);
    }

    @Override
    public List<CourseProgress> getAllProgressByStudent(String studentId) {
        return courseProgressRepository.findByStudentId(studentId);
    }

    @Override 
    public void saveProgress(CourseProgress courseProgress) {
        if (courseProgress.getProgressPercentage() < 0 || courseProgress.getProgressPercentage() > 100) {
            throw new IllegalArgumentException("Progress percentage must be between 0 and 100.");
        }
        if (courseProgress.getLastWatchedPosition() < 0) {
            throw new IllegalArgumentException("Last watched position cannot be negative.");
        }
        courseProgressRepository.save(courseProgress);
    }
    
    @Override
    public double calculateOverallProgress(String studentId, Integer courseId) {
        List<CourseProgress> progressList = courseProgressRepository.findByStudentId(studentId);
        // Filter records for this course
        List<CourseProgress> courseProgresses = progressList.stream()
                .filter(cp -> cp.getCourseId().equals(courseId))
                .collect(Collectors.toList());
        if (courseProgresses.isEmpty()) {
            return 0.0;
        }
        double totalProgress = courseProgresses.stream()
                .mapToDouble(CourseProgress::getProgressPercentage)
                .sum();
        return totalProgress / courseProgresses.size();
    }
    
    @Override
    public List<CourseProgress> getAllCourseProgress() {
        return courseProgressRepository.findAll();
    }
}
