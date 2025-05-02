package com.lms.cdac.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lms.cdac.entities.Badge;
import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.User;
import com.lms.cdac.entities.QuizAttempt;
import com.lms.cdac.services.BadgeService;
import com.lms.cdac.services.ClassAttendanceService;
import com.lms.cdac.services.CourseProgressService;
import com.lms.cdac.services.LabAttendanceService;
import com.lms.cdac.services.QuizAttemptService;
import com.lms.cdac.services.QuizAssignmentService;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BadgeServiceImpl implements BadgeService {

    private final ClassAttendanceService classAttendanceService;
    private final CourseProgressService courseProgressService;
    private final LabAttendanceService labAttendanceService;
    private final QuizAttemptService quizAttemptService;
    private final QuizAssignmentService quizAssignmentService;

    // Weights for different components
    private static final double CLASS_ATTENDANCE_WEIGHT = 0.20; // 20%
    private static final double COURSE_PROGRESS_WEIGHT = 0.15;  // 15%
    private static final double LAB_COMPONENT_WEIGHT = 0.25;    // 25%
    private static final double QUIZ_COMPONENT_WEIGHT = 0.40;   // 40%

    @Override
    public Badge calculateBadge(User student, Course course) {
        double overallProgress = calculateOverallProgress(student, course);
        
        if (overallProgress >= 90.0) {
            return Badge.GREEN;
        } else if (overallProgress >= 70.0) {
            return Badge.BLUE;
        } else {
            return Badge.YELLOW;
        }
    }

    @Override
    public double calculateOverallProgress(User student, Course course) {
        double totalWeight = 0.0;
        double weightedSum = 0.0;

        // Class Attendance Component
        var attendances = classAttendanceService.getAttendanceByStudentAndCourse(student, course);
        if (!attendances.isEmpty()) {
            double classAttendanceComponent = calculateClassAttendanceComponent(student, course);
            weightedSum += classAttendanceComponent * CLASS_ATTENDANCE_WEIGHT;
            totalWeight += CLASS_ATTENDANCE_WEIGHT;
        }

        // Course Progress Component
        double courseProgress = courseProgressService.calculateOverallProgress(student.getUserId(), course.getId());
        if (courseProgress > 0) {
            weightedSum += courseProgress * COURSE_PROGRESS_WEIGHT;
            totalWeight += COURSE_PROGRESS_WEIGHT;
        }

        // Lab Component
        var labAttendances = labAttendanceService.getAttendanceByStudentAndCourse(student, course);
        if (!labAttendances.isEmpty()) {
            double labComponent = calculateLabComponent(student, course);
            weightedSum += labComponent * LAB_COMPONENT_WEIGHT;
            totalWeight += LAB_COMPONENT_WEIGHT;
        }

        // Quiz Component
        var quizAssignments = quizAssignmentService.getAssignmentByCourse(course);
        if (quizAssignments != null && !quizAssignments.isEmpty()) {
            double quizComponent = calculateQuizComponent(student, course);
            weightedSum += quizComponent * QUIZ_COMPONENT_WEIGHT;
            totalWeight += QUIZ_COMPONENT_WEIGHT;
        }

        // If no components are available, return 0
        if (totalWeight == 0.0) {
            return 0.0;
        }

        // Calculate final weighted average based on available components
        return (weightedSum / totalWeight);
    }

    private double calculateClassAttendanceComponent(User student, Course course) {
        var attendances = classAttendanceService.getAttendanceByStudentAndCourse(student, course);
        if (attendances.isEmpty()) {
            return 0.0;
        }
        
        long presentCount = attendances.stream().filter(a -> a.isPresent()).count();
        return (presentCount * 100.0) / attendances.size();
    }

    private double calculateCourseProgressComponent(User student, Course course) {
        return courseProgressService.calculateOverallProgress(student.getUserId(), course.getId());
    }

    private double calculateLabComponent(User student, Course course) {
        var labAttendances = labAttendanceService.getAttendanceByStudentAndCourse(student, course);
        if (labAttendances.isEmpty()) {
            return 0.0; // Changed to return 0 instead of 100 when no lab component exists
        }

        // Calculate average of attendance (50%) and marks (50%)
        double attendanceScore = labAttendances.stream()
                .filter(a -> a.isPresent())
                .count() * 100.0 / labAttendances.size();

        double marksScore = labAttendances.stream()
                .mapToInt(a -> a.getMarks())
                .average()
                .orElse(0.0);

        return (attendanceScore * 0.5) + (marksScore * 0.5);
    }

    private double calculateQuizComponent(User student, Course course) {
        // Get all quiz assignments for the course (including module and topic level)
        var quizAssignments = quizAssignmentService.getAssignmentByCourse(course);
        
        // If no quizzes are assigned at any level, return full marks
        if (quizAssignments == null || quizAssignments.isEmpty()) {
            return 100.0;
        }

        // Get total number of quizzes assigned
        int totalQuizzes = quizAssignments.size();

        // Get all quiz attempts for this student and course
        var quizAttempts = quizAttemptService.getQuizAttemptsByStudentAndCourse(student, course);
        
        // If no attempts made but quizzes exist, return 0
        if (quizAttempts.isEmpty()) {
            return 0.0;
        }

        // Create a map of quiz ID to best attempt score
        Map<Long, Double> bestScores = quizAttempts.stream()
            .collect(Collectors.groupingBy(
                attempt -> attempt.getQuiz().getId(),
                Collectors.collectingAndThen(
                    Collectors.maxBy(Comparator.comparingDouble(QuizAttempt::getScorePercentage)),
                    optAttempt -> optAttempt.map(QuizAttempt::getScorePercentage).orElse(0.0)
                )
            ));

        // Calculate total score considering all assigned quizzes
        double totalScore = 0.0;
        for (var assignment : quizAssignments) {
            // Get the best score for this quiz, or 0 if not attempted
            double quizScore = bestScores.getOrDefault(assignment.getQuiz().getId(), 0.0);
            totalScore += quizScore;
        }

        // Return average score across all assigned quizzes
        return totalScore / totalQuizzes;
    }
} 