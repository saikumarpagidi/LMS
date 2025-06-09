package com.lms.cdac.services.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lms.cdac.dto.AnalyticsDTO;
import com.lms.cdac.dto.MetricsDTO;
import com.lms.cdac.dto.RCReportAggregateDTO;
import com.lms.cdac.dto.ResourceCenterSummaryDTO;
import com.lms.cdac.dto.StudentProgressDTO;
import com.lms.cdac.entities.College;
import com.lms.cdac.entities.Institution;
import com.lms.cdac.entities.User;
import com.lms.cdac.repositories.AssignRoleRepo;
import com.lms.cdac.repositories.CourseAssignmentRepository;
import com.lms.cdac.repsitories.CollegeRepository;
import com.lms.cdac.repsitories.CourseProgressRepository;
import com.lms.cdac.repsitories.InstitutionRepository;
import com.lms.cdac.repsitories.UserRepositories;
import com.lms.cdac.services.AnalyticsService;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    @Autowired
    private CourseProgressRepository        cpRepo;

    @Autowired
    private CourseAssignmentRepository      caRepo;

    @Autowired
    private AssignRoleRepo                  roleRepo;

    @Autowired
    private UserRepositories                userRepo;

    @Autowired
    private InstitutionRepository           instRepo;

    @Autowired
    private CollegeRepository               collegeRepo;

    // ─── EXISTING (unfiltered) ─────────────────────────────────────────────

    @Override
    public List<AnalyticsDTO> getCourseCompletionStats() {
        return cpRepo.findAverageProgressPerCourse();
    }

    @Override
    public List<AnalyticsDTO> getCourseEnrollmentStats() {
        return caRepo.findEnrollmentPerCourse();
    }

    @Override
    public List<AnalyticsDTO> getActiveUsersStats() {
        return cpRepo.findDistinctActiveUsers();
    }

    @Override
    public List<AnalyticsDTO> getWithdrawalStats() {
        return List.of(new AnalyticsDTO("Withdrawals", cpRepo.countWithdrawals()));
    }

    @Override
    public List<AnalyticsDTO> getUsersByResourceCenter() {
        return roleRepo.countUsersByResourceCenter();
    }

    @Override
    public Long getTotalUsers() {
        return userRepo.countAllUsers();
    }

    // ─── FILTERED VERSIONS ─────────────────────────────────────────────────

    @Override
    public List<AnalyticsDTO> getCourseCompletionStats(List<String> rcs, List<String> cols) {
        if ((rcs == null || rcs.isEmpty()) && (cols == null || cols.isEmpty())) {
            return getCourseCompletionStats();
        }
        return cpRepo.findAverageProgressPerCourseFiltered(rcs, cols)
                     .stream()
                     .map(r -> new AnalyticsDTO((String) r[0], ((Number) r[1]).doubleValue()))
                     .collect(Collectors.toList());
    }

    @Override
    public List<AnalyticsDTO> getActiveUsersStats(List<String> rcs, List<String> cols) {
        if ((rcs == null || rcs.isEmpty()) && (cols == null || cols.isEmpty())) {
            return getActiveUsersStats();
        }
        Object[] row = cpRepo.findDistinctActiveUsersFiltered(rcs, cols);
        return List.of(new AnalyticsDTO("ActiveUsers", ((Number) row[1]).longValue()));
    }

    @Override
    public List<AnalyticsDTO> getUsersByResourceCenter(List<String> rcs, List<String> cols) {
        if ((rcs == null || rcs.isEmpty()) && (cols == null || cols.isEmpty())) {
            return getUsersByResourceCenter();
        }
        return cpRepo.findUsersByResourceCenterFiltered(rcs, cols)
                     .stream()
                     .map(r -> new AnalyticsDTO((String) r[0], ((Number) r[1]).longValue()))
                     .collect(Collectors.toList());
    }

    @Override
    public Map<String, Long> getTotals(List<String> rcs, List<String> cols) {
        if ((rcs == null || rcs.isEmpty()) && (cols == null || cols.isEmpty())) {
            return Map.of(
                "totalUsers",  getTotalUsers(),
                "enrollments", caRepo.countAllEnrollments(),
                "completions", cpRepo.countCompleted(),
                "withdrawals", cpRepo.countWithdrawals()
            );
        }
        Object[] row = cpRepo.findTotalsFiltered(rcs, cols);
        return Map.of(
            "totalUsers",  ((Number) row[0]).longValue(),
            "enrollments", ((Number) row[1]).longValue(),
            "completions", ((Number) row[2]).longValue(),
            "withdrawals", ((Number) row[3]).longValue()
        );
    }

    // ─── LOOKUPS & METRICS ────────────────────────────────────────────────

    @Override
    public List<String> getAllPmus() {
        return instRepo.findDistinctLocations();
    }

    @Override
    public List<String> getAllResourceCenters() {
        return userRepo.findDistinctResourceCenters();
    }

    @Override
    public List<Institution> getResourceCentersByPmus(List<String> pmus) {
        return instRepo.findByLocationIn(pmus);
    }

    @Override
    public List<College> getCollegesByResourceCenters(List<String> rcs) {
        return rcs.stream()
                   .flatMap(rc -> instRepo.findByResourceCenter(rc)
                       .map(i -> collegeRepo.findByResourceCenter(i).stream())
                       .orElseGet(Stream::empty))
                   .collect(Collectors.toList());
    }

    @Override
    public Map<String, Long> getStudentCountByColleges(List<String> colleges) {
        return colleges.stream()
                        .collect(Collectors.toMap(
                            c -> c,
                            c -> userRepo.countByCollegeIn(List.of(c))
                        ));
    }

    @Override
    public List<MetricsDTO> getResourceCenterMetrics(List<String> rcs) {
        return cpRepo.findMetricsByResourceCenters(rcs)
                     .stream()
                     .map(r -> new MetricsDTO((String) r[0],
                                              ((Number) r[1]).longValue(),
                                              ((Number) r[2]).longValue()))
                     .collect(Collectors.toList());
    }

    @Override
    public List<MetricsDTO> getCollegeMetrics(List<String> cols) {
        List<String> targetCols;
        if (cols == null || cols.isEmpty()) {
            targetCols = collegeRepo.findAll()
                                     .stream()
                                     .map(College::getCollegeName)
                                     .collect(Collectors.toList());
        } else {
            targetCols = cols;
        }
        return cpRepo.findMetricsByColleges(targetCols)
                     .stream()
                     .map(r -> new MetricsDTO((String) r[0],
                                              ((Number) r[1]).longValue(),
                                              ((Number) r[2]).longValue()))
                     .collect(Collectors.toList());
    }

    // ─── RC-LEVEL REPORT ───────────────────────────────────────────────────

    @Override
    public RCReportAggregateDTO getRCReport(String pmu, String rc) {
        List<User> students = userRepo.findByResourceCenter(rc);

        RCReportAggregateDTO dto = new RCReportAggregateDTO();
        dto.setPmu(pmu);
        dto.setResourceCenter(rc);
        dto.setTotalStudents((long) students.size());
        dto.setStudentsWithAssignments(caRepo.countDistinctStudentsWithAssignment(rc));
        dto.setStudentsCompletedAny(cpRepo.countCompletedStudentsByRC(rc));
        dto.setTotalAssignments(caRepo.countAssignmentsByResourceCenter(rc));
        dto.setTotalCompletedCourses(cpRepo.countTotalCompletedCoursesByRC(rc));

        List<StudentProgressDTO> progressList = students.stream().map(u -> {
            long assigned = caRepo.countAssignmentsByStudentAndRC(u.getUserId(), rc);
            long done     = cpRepo.countCompletedByStudentAndRC(u.getUserId(), rc);
            double pct    = assigned == 0 ? 0.0 : (done * 100.0 / assigned);
            return new StudentProgressDTO(u.getUserId(), u.getName(), assigned, done, pct);
        }).collect(Collectors.toList());

        dto.setStudentProgressList(progressList);
        return dto;
    }

    @Override
    public List<StudentProgressDTO> getProgressByResourceCenter(String rc) {
        return getRCReport("", rc).getStudentProgressList();
    }

    @Override
    public double getStudentCompletionRate() {
        long tot  = caRepo.countDistinctAssignedStudents();
        long full = cpRepo.countDistinctStudentsWithFullCompletion();
        return tot == 0 ? 0.0 : (full * 100.0 / tot);
    }

    // ─── PER-RC BREAKDOWN ───────────────────────────────────────────────────

    private List<AnalyticsDTO> toDto(List<Object[]> rows) {
        return rows.stream()
                   .map(r -> new AnalyticsDTO((String) r[0], ((Number) r[1]).longValue()))
                   .collect(Collectors.toList());
    }

    @Override
    public List<AnalyticsDTO> getStudentsCountByRC(List<String> rcs) {
        return toDto(userRepo.countStudentsByResourceCenter())
               .stream()
               .filter(d -> rcs.contains(d.getCategory()))
               .collect(Collectors.toList());
    }

    @Override
    public List<AnalyticsDTO> getAssignmentsCountByRC(List<String> rcs) {
        return toDto(caRepo.countAssignmentsByResourceCenterAll())
               .stream()
               .filter(d -> rcs.contains(d.getCategory()))
               .collect(Collectors.toList());
    }

    @Override
    public List<AnalyticsDTO> getCompletionsCountByRC(List<String> rcs) {
        return toDto(cpRepo.countCompletionsByResourceCenter())
               .stream()
               .filter(d -> rcs.contains(d.getCategory()))
               .collect(Collectors.toList());
    }

    @Override
    public List<AnalyticsDTO> getOngoingCountByRC(List<String> rcs) {
        return toDto(cpRepo.countOngoingByResourceCenter())
               .stream()
               .filter(d -> rcs.contains(d.getCategory()))
               .collect(Collectors.toList());
    }

    @Override
    public List<ResourceCenterSummaryDTO> getPmuRCsSummary(String pmu) {
        List<Institution> insts = instRepo.findByLocation(pmu);
        List<String> rcs = insts.stream()
                                 .map(Institution::getResourceCenter)
                                 .collect(Collectors.toList());

        return rcs.stream().map(rc -> {
            long students    = userRepo.countByResourceCenter(rc);
            long assignments = caRepo.countAssignmentsByResourceCenter(rc);
            long completions = cpRepo.countTotalCompletedCoursesByRC(rc);
            long pending     = assignments - completions;
            return new ResourceCenterSummaryDTO(rc, students, assignments, completions, pending);
        }).collect(Collectors.toList());
    }
    
    @Override
    public List<AnalyticsDTO> getCourseEnrollmentStats(List<String> resourceCenters) {
        if (resourceCenters == null || resourceCenters.isEmpty()) {
            // अगर कोई RC नहीं दी गई, तो पुराने मेथड को कॉल करो
            return getCourseEnrollmentStats();
        }
        // रिपॉजिटरी का नया मेथड कॉल करें, जो केवल उन RCs के लिए GROUP BY करता है
        return caRepo.findEnrollmentPerCourseByRCs(resourceCenters);
        
        
        
        
        
    }


}
