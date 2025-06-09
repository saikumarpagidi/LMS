package com.lms.cdac.services;

import com.lms.cdac.dto.AnalyticsDTO;
import com.lms.cdac.dto.MetricsDTO;
import com.lms.cdac.dto.RCReportAggregateDTO;
import com.lms.cdac.dto.ResourceCenterSummaryDTO;
import com.lms.cdac.dto.StudentProgressDTO;
import com.lms.cdac.entities.College;
import com.lms.cdac.entities.Institution;

import java.util.List;
import java.util.Map;

public interface AnalyticsService {

    // EXISTING (unfiltered)
    List<AnalyticsDTO> getCourseCompletionStats();
    List<AnalyticsDTO> getCourseEnrollmentStats();
    List<AnalyticsDTO> getActiveUsersStats();
    List<AnalyticsDTO> getWithdrawalStats();
    List<AnalyticsDTO> getUsersByResourceCenter();
    Long               getTotalUsers();

    // OVERLOADED FOR FILTERED QUERIES
    List<AnalyticsDTO> getCourseCompletionStats(List<String> resourceCenters, List<String> colleges);
    List<AnalyticsDTO> getActiveUsersStats      (List<String> resourceCenters, List<String> colleges);
    List<AnalyticsDTO> getUsersByResourceCenter (List<String> resourceCenters, List<String> colleges);
    Map<String, Long>  getTotals                (List<String> resourceCenters, List<String> colleges);

    // LOOKUPS
    List<String>      getAllPmus();
    List<String>      getAllResourceCenters();
    List<Institution> getResourceCentersByPmus     (List<String> pmus);
    List<College>     getCollegesByResourceCenters(List<String> resourceCenters);

    // METRICS
    Map<String, Long> getStudentCountByColleges    (List<String> colleges);
    List<MetricsDTO>  getResourceCenterMetrics     (List<String> resourceCenters);
    List<MetricsDTO>  getCollegeMetrics            (List<String> colleges);

    // RC-LEVEL REPORT
    RCReportAggregateDTO     getRCReport(String pmu, String resourceCenter);
    List<StudentProgressDTO> getProgressByResourceCenter(String resourceCenter);
    double                   getStudentCompletionRate();

    // PER-RC BREAKDOWN
    List<AnalyticsDTO> getStudentsCountByRC    (List<String> resourceCenters);
    List<AnalyticsDTO> getAssignmentsCountByRC (List<String> resourceCenters);
    List<AnalyticsDTO> getCompletionsCountByRC (List<String> resourceCenters);
    List<AnalyticsDTO> getOngoingCountByRC     (List<String> resourceCenters);
    
    List<ResourceCenterSummaryDTO> getPmuRCsSummary(String pmu);
    
    List<AnalyticsDTO> getCourseEnrollmentStats(List<String> resourceCenters);
    
    

}