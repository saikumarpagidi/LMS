package com.lms.cdac.repsitories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lms.cdac.dto.AnalyticsDTO;
import com.lms.cdac.entities.CourseProgress;

public interface CourseProgressRepository extends JpaRepository<CourseProgress, Long> {

    // ─── Basic Finders ────────────────────────────────────────────────────

    Optional<CourseProgress> findByStudentIdAndCourseIdAndResourceId(
        String studentId, Integer courseId, Integer resourceId);

    List<CourseProgress> findByStudentId(String studentId);

    // ─── AnalyticsDTO Queries (JPQL) ─────────────────────────────────────

    @Query("SELECT new com.lms.cdac.dto.AnalyticsDTO(CONCAT('Course-', cp.courseId), AVG(cp.progressPercentage)) " +
           "FROM CourseProgress cp GROUP BY cp.courseId")
    List<AnalyticsDTO> findAverageProgressPerCourse();

    @Query("SELECT new com.lms.cdac.dto.AnalyticsDTO('ActiveUsers', COUNT(DISTINCT cp.studentId)) " +
           "FROM CourseProgress cp")
    List<AnalyticsDTO> findDistinctActiveUsers();

    // ─── RC-specific counts (JPQL via User.resourceCenter) ─────────────────

    @Query("SELECT COUNT(cp) " +
           "FROM CourseProgress cp " +
           "JOIN User u ON u.userId = cp.studentId " +
           "WHERE u.resourceCenter = :rc " +
           "  AND cp.studentId = :studentId " +
           "  AND cp.progressPercentage = 100.0")
    long countCompletedByStudentAndRC(
        @Param("studentId") String studentId,
        @Param("rc") String rc);

    @Query("SELECT COUNT(DISTINCT cp.studentId) " +
           "FROM CourseProgress cp " +
           "JOIN User u ON u.userId = cp.studentId " +
           "WHERE u.resourceCenter = :rc")
    long countDistinctStudentsWithProgressByRC(@Param("rc") String rc);

    @Query("SELECT COUNT(cp) " +
           "FROM CourseProgress cp " +
           "JOIN User u ON u.userId = cp.studentId " +
           "WHERE u.resourceCenter = :rc " +
           "  AND cp.progressPercentage = 100.0")
    long countTotalCompletedCoursesByRC(@Param("rc") String rc);

    @Query("SELECT COUNT(DISTINCT cp.studentId) " +
           "FROM CourseProgress cp " +
           "JOIN User u ON u.userId = cp.studentId " +
           "WHERE u.resourceCenter = :rc " +
           "  AND cp.progressPercentage = 100.0")
    long countCompletedStudentsByRC(@Param("rc") String rc);

    @Query("SELECT COUNT(DISTINCT cp.studentId) " +
           "FROM CourseProgress cp " +
           "JOIN User u ON u.userId = cp.studentId " +
           "WHERE u.resourceCenter = :rc " +
           "  AND cp.progressPercentage < 100.0")
    long countOngoingStudentsByRC(@Param("rc") String rc);

    // ─── Overall Stats (JPQL) ─────────────────────────────────────────────

    @Query("SELECT COUNT(cp) FROM CourseProgress cp WHERE cp.progressPercentage = 100.0")
    long countCompleted();

    @Query("SELECT COUNT(cp) FROM CourseProgress cp WHERE cp.progressPercentage < 100.0")
    long countWithdrawals();

    @Query("SELECT COUNT(DISTINCT cp.studentId) FROM CourseProgress cp WHERE cp.progressPercentage = 100.0")
    long countDistinctStudentsWithFullCompletion();

    // ─── RC-wise breakdown (distinct students who have completed / ongoing) ──

    @Query("SELECT u.resourceCenter AS category, COUNT(DISTINCT cp.studentId) AS value " +
           "FROM CourseProgress cp " +
           "JOIN User u ON u.userId = cp.studentId " +
           "WHERE cp.progressPercentage = 100.0 " +
           "GROUP BY u.resourceCenter")
    List<Object[]> countCompletionsByResourceCenter();

    @Query("SELECT u.resourceCenter AS category, COUNT(DISTINCT cp.studentId) AS value " +
           "FROM CourseProgress cp " +
           "JOIN User u ON u.userId = cp.studentId " +
           "WHERE cp.progressPercentage < 100.0 " +
           "GROUP BY u.resourceCenter")
    List<Object[]> countOngoingByResourceCenter();

    // ─── Metrics by Resource Center (JPQL via User.resourceCenter) ─────────

    @Query("SELECT u.resourceCenter AS category, " +
           "       COUNT(DISTINCT cp.studentId)               AS enrolled, " +
           "       SUM(CASE WHEN cp.progressPercentage = 100.0 THEN 1 ELSE 0 END) AS completed " +
           "FROM CourseProgress cp " +
           "JOIN User u ON u.userId = cp.studentId " +
           "WHERE (:#{#rcs == null or #rcs.isEmpty()} = true " +
           "       OR u.resourceCenter IN (:rcs)) " +
           "GROUP BY u.resourceCenter")
    List<Object[]> findMetricsByResourceCenters(@Param("rcs") List<String> rcs);

    // ─── Metrics by College (JPQL via User.college String) ────────────────────

    @Query("SELECT u.college AS category, " +
           "       COUNT(DISTINCT cp.studentId)           AS enrolled, " +
           "       SUM(CASE WHEN cp.progressPercentage = 100.0 THEN 1 ELSE 0 END) AS completed " +
           "FROM CourseProgress cp " +
           "JOIN User u ON u.userId = cp.studentId " +
           "WHERE (:#{#cols == null or #cols.isEmpty()} = true " +
           "       OR u.college IN (:cols)) " +
           "GROUP BY u.college")
    List<Object[]> findMetricsByColleges(@Param("cols") List<String> cols);

    // ─── Filtered Summaries (Native SQL) ─────────────────────────────────

    @Query(value = """
        SELECT
          COUNT(DISTINCT cp.student_id)                                     AS totalUsers,
          COUNT(cp.id)                                                      AS enrollments,
          SUM(CASE WHEN cp.progress_percentage = 100 THEN 1 ELSE 0 END)      AS completions,
          SUM(CASE WHEN cp.progress_percentage <  100 THEN 1 ELSE 0 END)     AS withdrawals
        FROM course_progress cp
        JOIN course_resource cr ON cp.resource_id       = cr.id
        JOIN colleges       c  ON cr.college_id         = c.id
        JOIN institution    i  ON c.resource_center_id  = i.id
        WHERE (:#{#rcs == null or #rcs.isEmpty()} = true OR i.resource_center IN (:rcs))
          AND (:#{#cols == null or #cols.isEmpty()} = true OR c.college_name    IN (:cols))
        """, nativeQuery = true)
    Object[] findTotalsFiltered(
        @Param("rcs") List<String> rcs,
        @Param("cols") List<String> cols);

    @Query(value = """
        SELECT
          CONCAT('Course-', cp.course_id)    AS category,
          AVG(cp.progress_percentage)       AS value
        FROM course_progress cp
        JOIN course_resource cr ON cp.resource_id       = cr.id
        JOIN colleges       c  ON cr.college_id         = c.id
        JOIN institution    i  ON c.resource_center_id  = i.id
        WHERE (:#{#rcs == null or #rcs.isEmpty()} = true OR i.resource_center IN (:rcs))
          AND (:#{#cols == null or #cols.isEmpty()} = true OR c.college_name    IN (:cols))
        GROUP BY cp.course_id
        """, nativeQuery = true)
    List<Object[]> findAverageProgressPerCourseFiltered(
        @Param("rcs") List<String> rcs,
        @Param("cols") List<String> cols);

    @Query(value = """
        SELECT
          'ActiveUsers'                     AS category,
          COUNT(DISTINCT cp.student_id)     AS value
        FROM course_progress cp
        JOIN course_resource cr ON cp.resource_id       = cr.id
        JOIN colleges       c  ON cr.college_id         = c.id
        JOIN institution    i  ON c.resource_center_id  = i.id
        WHERE (:#{#rcs == null or #rcs.isEmpty()} = true OR i.resource_center IN (:rcs))
          AND (:#{#cols == null or #cols.isEmpty()} = true OR c.college_name    IN (:cols))
        """, nativeQuery = true)
    Object[] findDistinctActiveUsersFiltered(
        @Param("rcs") List<String> rcs,
        @Param("cols") List<String> cols);

    @Query(value = """
        SELECT
          i.resource_center                  AS category,
          COUNT(DISTINCT cp.student_id)      AS value
        FROM course_progress cp
        JOIN course_resource cr ON cp.resource_id       = cr.id
        JOIN colleges       c  ON cr.college_id         = c.id
        JOIN institution    i  ON c.resource_center_id  = i.id
        WHERE (:#{#rcs == null or #rcs.isEmpty()} = true OR i.resource_center IN (:rcs))
          AND (:#{#cols == null or #cols.isEmpty()} = true OR c.college_name    IN (:cols))
        GROUP BY i.resource_center
        """, nativeQuery = true)
    List<Object[]> findUsersByResourceCenterFiltered(
        @Param("rcs") List<String> rcs,
        @Param("cols") List<String> cols);

    @Query(value = """
        SELECT
          i.resource_center                                   AS category,
          COUNT(DISTINCT cp.student_id)                       AS enrolled,
          SUM(CASE WHEN cp.progress_percentage = 100 THEN 1 ELSE 0 END) AS completed
        FROM course_progress cp
        JOIN course_resource cr ON cp.resource_id       = cr.id
        JOIN colleges       c  ON cr.college_id         = c.id
        JOIN institution    i  ON c.resource_center_id  = i.id
        WHERE (:#{#rcs == null or #rcs.isEmpty()} = true OR i.resource_center IN (:rcs))
        GROUP BY i.resource_center
        """, nativeQuery = true)
    List<Object[]> findMetricsByResourceCenters1(@Param("rcs") List<String> rcs);

    @Query(value = """
        SELECT
          c.college_name                                      AS category,
          COUNT(DISTINCT cp.student_id)                       AS enrolled,
          SUM(CASE WHEN cp.progress_percentage = 100 THEN 1 ELSE 0 END) AS completed
        FROM course_progress cp
        JOIN course_resource cr ON cp.resource_id       = cr.id
        JOIN colleges       c  ON cr.college_id         = c.id
        WHERE (:#{#cols == null or #cols.isEmpty()} = true OR c.college_name IN (:cols))
        GROUP BY c.college_name
        """, nativeQuery = true)
    List<Object[]> findMetricsByColleges1(@Param("cols") List<String> cols);
}
