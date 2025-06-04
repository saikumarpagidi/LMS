package com.lms.cdac.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class StudentProgressDTO {
    private String studentId;
    private String studentName;
    private long assignedCount;
    private long completedCount;
    private double completionPercent;

    public StudentProgressDTO(String studentId, String studentName,
                              long assignedCount, long completedCount, double completionPercent) {
        this.studentId       = studentId;
        this.studentName     = studentName;
        this.assignedCount   = assignedCount;
        this.completedCount  = completedCount;
        this.completionPercent = completionPercent;
    }
}
