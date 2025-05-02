package com.lms.cdac.services;

import com.lms.cdac.entities.Badge;
import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.User;
 
public interface BadgeService {
    Badge calculateBadge(User student, Course course);
    double calculateOverallProgress(User student, Course course);
} 