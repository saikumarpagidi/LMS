package com.lms.cdac.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;

import com.lms.cdac.entities.CourseAssignment;
import com.lms.cdac.entities.Course;
import com.lms.cdac.entities.Quiz;
import com.lms.cdac.entities.User;
import com.lms.cdac.dto.AnalyticsDTO;

import jakarta.transaction.Transactional;

public interface CourseAssignmentRepository extends JpaRepository<CourseAssignment, Long> {

    List<CourseAssignment> findByUser(User user);

    List<CourseAssignment> findByCourse(Course course);

    @Modifying
    @Transactional
    @Query("DELETE FROM QuizAssignment qa WHERE qa.quiz = :quiz")
    void deleteByQuiz(@Param("quiz") Quiz quiz);

    @Query("SELECT ca FROM CourseAssignment ca WHERE ca.user = :user")
    List<CourseAssignment> findByUserWithRole(@Param("user") User user);

    @Query(
        "SELECT new com.lms.cdac.dto.AnalyticsDTO(" +
        "   ca.course.courseName, COUNT(ca.id)" +
        ") FROM CourseAssignment ca GROUP BY ca.course.courseName"
    )
    List<AnalyticsDTO> findEnrollmentPerCourse();

    // ────────────── मौजूदा RC-लेवल मेथड्स ──────────────

    @Query(
        "SELECT COUNT(DISTINCT ca.user.id) " +
        "FROM CourseAssignment ca " +
        "WHERE ca.user.resourceCenter = :rc"
    )
    long countDistinctStudentsWithAssignment(@Param("rc") String resourceCenter);

    @Query(
        "SELECT COUNT(ca) " +
        "FROM CourseAssignment ca " +
        "WHERE ca.user.resourceCenter = :rc"
    )
    long countAssignmentsByResourceCenter(@Param("rc") String resourceCenter);

    @Query(
        "SELECT COUNT(ca) " +
        "FROM CourseAssignment ca " +
        "WHERE ca.user.resourceCenter = :rc " +
        "  AND ca.user.id = :studentId"
    )
    long countAssignmentsByStudentAndRC(
        @Param("studentId") String studentId,
        @Param("rc") String resourceCenter
    );

    @Query("SELECT COUNT(ca.id) FROM CourseAssignment ca")
    long countAllEnrollments();

    @Query("SELECT COUNT(DISTINCT ca.user.id) FROM CourseAssignment ca")
    long countDistinctAssignedStudents();

    // ────────────── नए Query Methods ──────────────

    /** RC में कितनी Assignments हुईं */
    @Query(
        "SELECT ca.user.resourceCenter AS category, COUNT(ca) AS value " +
        "FROM CourseAssignment ca GROUP BY ca.user.resourceCenter"
    )
    List<Object[]> countAssignmentsByResourceCenterAll();

    /** College में कितनी Assignments हुईं */
    @Query(
        "SELECT ca.user.college AS category, COUNT(ca) AS value " +
        "FROM CourseAssignment ca GROUP BY ca.user.college"
    )
    List<Object[]> countAssignmentsByCollegeAll();

    @Query(
        "SELECT COUNT(DISTINCT ca.user.userId) " +
        "FROM CourseAssignment ca " +
        "WHERE ca.user.resourceCenter = :rc"
    )
    long countAssignedStudentsByRC(@Param("rc") String resourceCenter);

    @Query(
        "SELECT new com.lms.cdac.dto.AnalyticsDTO(" +
        "   ca.course.courseName, COUNT(ca.id)" +
        ") FROM CourseAssignment ca " +
        "WHERE ca.user.resourceCenter IN :rcs " +
        "GROUP BY ca.course.courseName"
    )
    List<AnalyticsDTO> findEnrollmentPerCourseByRCs(@Param("rcs") List<String> resourceCenters);

    /** केवल एक Resource Center के लिए Course-wise Enrollment */
    @Query(
        "SELECT new com.lms.cdac.dto.AnalyticsDTO(" +
        "   ca.course.courseName, COUNT(ca.id)" +
        ") FROM CourseAssignment ca " +
        "WHERE ca.user.resourceCenter = :rc " +
        "GROUP BY ca.course.courseName"
    )
    List<AnalyticsDTO> findEnrollmentPerCourseByRC(@Param("rc") String resourceCenter);
}
