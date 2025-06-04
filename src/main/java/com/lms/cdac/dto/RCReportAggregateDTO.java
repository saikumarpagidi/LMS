package com.lms.cdac.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class RCReportAggregateDTO {
    private String pmu;
    private String resourceCenter;
    private long totalStudents;
    private long studentsWithAssignments;
    private long studentsCompletedAny;
    private long totalAssignments;
    private long totalCompletedCourses;
    private List<StudentProgressDTO> studentProgressList;
}
