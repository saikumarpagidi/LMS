package com.lms.cdac.controller;

import com.lms.cdac.dto.AnalyticsDTO;
import com.lms.cdac.dto.MetricsDTO;
import com.lms.cdac.dto.RCReportAggregateDTO;
import com.lms.cdac.dto.ResourceCenterSummaryDTO;
import com.lms.cdac.dto.StudentProgressDTO;
import com.lms.cdac.services.AnalyticsService;
import com.lms.cdac.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService svc;
    
    @Autowired
    private UserService userService;

    /**
     * Thymeleaf टेम्प्लेट रिटर्न करता है (view name)
     */
    @GetMapping("/dashboard")
    public String dashboard() {
        return "analytics_dashboard";  // Thymeleaf टेम्प्लेट फाइल: analytics_dashboard.html
    }

    // ─────────────────────────────────── BASIC ENDPOINTS ───────────────────────────────────

    @GetMapping("/course-enrollment")
    @ResponseBody
    public List<AnalyticsDTO> courseEnrollment() {
        return svc.getCourseEnrollmentStats();
    }

    @GetMapping("/course-completion")
    @ResponseBody
    public List<AnalyticsDTO> courseCompletion() {
        return svc.getCourseCompletionStats();
    }

    @GetMapping("/active-users")
    @ResponseBody
    public List<AnalyticsDTO> activeUsers() {
        return svc.getActiveUsersStats();
    }

    @GetMapping("/by-resource-center")
    @ResponseBody
    public List<AnalyticsDTO> byRC() {
        return svc.getUsersByResourceCenter();
    }

    @GetMapping("/totals")
    @ResponseBody
    public Map<String, Long> totals() {
        // Filter की खाली लिस्ट भेजने पर unfiltered टोटल लौटेगा
        return svc.getTotals(List.of(), List.of());
    }

    // ─── FILTERS: PMU → RC → College ─────────────────────────────────────────

    @GetMapping("/pmus")
    @ResponseBody
    public List<String> pmus() {
        return svc.getAllPmus();
    }

    @GetMapping("/resource-centers")
    @ResponseBody
    public List<com.lms.cdac.entities.Institution> rcs(@RequestParam List<String> pmus) {
        return svc.getResourceCentersByPmus(pmus);
    }

    @GetMapping("/colleges")
    @ResponseBody
    public List<com.lms.cdac.entities.College> colleges(@RequestParam List<String> rcs) {
        return svc.getCollegesByResourceCenters(rcs);
    }

    // ─── METRICS ───────────────────────────────────────────────────────────────

    @GetMapping("/resource-centers/metrics")
    @ResponseBody
    public List<MetricsDTO> rcMetrics(@RequestParam List<String> rcs) {
        return svc.getResourceCenterMetrics(rcs);
    }

    @GetMapping("/colleges/metrics")
    @ResponseBody
    public List<MetricsDTO> cMetrics(
            @RequestParam(name = "cols", required = false) List<String> cols
    ) {
        if (cols == null || cols.isEmpty()) {
            // कोई specific colleges न हों → सभी लौटाएँ
            return svc.getCollegeMetrics(List.of());
        }
        return svc.getCollegeMetrics(cols);
    }

    // ─── RC-AGGREGATE REPORT ────────────────────────────────────────────────────

    @GetMapping("/rc-report")
    @ResponseBody
    public RCReportAggregateDTO rcReport(
            @RequestParam String pmu,
            @RequestParam String resourceCenter) {
        return svc.getRCReport(pmu, resourceCenter);
    }

    @GetMapping("/rc-progress")
    @ResponseBody
    public List<StudentProgressDTO> rcProgress(@RequestParam String resourceCenter) {
        return svc.getProgressByResourceCenter(resourceCenter);
    }

    // ─── PER-RC BREAKDOWN CHARTS ────────────────────────────────────────────────

    @GetMapping("/rc/students")
    @ResponseBody
    public List<AnalyticsDTO> rcStudents(@RequestParam List<String> rcs) {
        return svc.getStudentsCountByRC(rcs);
    }

    @GetMapping("/rc/assignments")
    @ResponseBody
    public List<AnalyticsDTO> rcAssignments(@RequestParam List<String> rcs) {
        return svc.getAssignmentsCountByRC(rcs);
    }

    @GetMapping("/rc/completions")
    @ResponseBody
    public List<AnalyticsDTO> rcCompletions(@RequestParam List<String> rcs) {
        return svc.getCompletionsCountByRC(rcs);
    }

    @GetMapping("/rc/ongoing")
    @ResponseBody
    public List<AnalyticsDTO> rcOngoing(@RequestParam List<String> rcs) {
        return svc.getOngoingCountByRC(rcs);
    }

    // ─── STUDENT COMPLETION RATE ────────────────────────────────────────────────

    @GetMapping("/student-completion-rate")
    @ResponseBody
    public Map<String, Double> completionRate() {
        return Map.of("completionRate", svc.getStudentCompletionRate());
    }

    // ─── PMU के अंतर्गत सभी RCs का सारांश ────────────────────────────────────────

    @GetMapping("/pmu/{pmu}/rc-summary")
    @ResponseBody
    public List<ResourceCenterSummaryDTO> pmuRcSummary(@PathVariable String pmu) {
        return svc.getPmuRCsSummary(pmu);
    }

    // ─── USER COUNTS BY RESOURCE CENTER ─────────────────────────────────────────

    @GetMapping("/users-by-resource-center")
    public ResponseEntity<List<AnalyticsDTO>> getUserCountByResourceCenter() {
        List<AnalyticsDTO> data = userService.getUsersByResourceCenter();
        return ResponseEntity.ok(data);
    }
    
    @GetMapping("/rc-college-registrations")
    @ResponseBody
    public ResponseEntity<List<AnalyticsDTO>> getCollegeRegistrationsByRC(
            @RequestParam("resourceCenter") String resourceCenter) {
        List<AnalyticsDTO> data = userService.getCollegeRegistrationsByResourceCenter(resourceCenter);
        return ResponseEntity.ok(data);
    }
    
    /**
     * Student Count by Name & Center (filtered by selected Resource Center)
     * अगर `resourceCenter` पैरामीटर भेजा गया है, तो उसी RC का डेटा लौटाएगा।
     * अगर पैरामीटर नहीं दिया गया, तो बिना फ़िल्टर सभी RCs का डेटा लौटाएगा।
     */
    @GetMapping("/student-count-by-name-and-center")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getStudentCountByNameAndCenter(
            @RequestParam(name = "resourceCenter", required = false) String resourceCenter) {

        List<Object[]> results;
        if (resourceCenter == null || resourceCenter.isBlank()) {
            // बिना फ़िल्टर सभी RCs का डेटा
            results = userService.countStudentsByNameAndResourceCenter();
        } else {
            // केवल दिए हुए Resource Center का डेटा
            results = userService.countStudentsByNameAndSpecificResourceCenter(resourceCenter);
        }

        List<Map<String, Object>> response = results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", row[0]);                // u.user_name
            map.put("resourceCenter", row[1]);      // u.resource_center
            map.put("count", ((Number) row[2]).longValue());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
