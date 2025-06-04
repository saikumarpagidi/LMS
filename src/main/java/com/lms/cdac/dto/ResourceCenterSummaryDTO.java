package com.lms.cdac.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class ResourceCenterSummaryDTO {
    private String resourceCenter;
    private long totalStudents;
    private long totalAssignments;
    private long totalCompletions;
    private long totalPending;
}
