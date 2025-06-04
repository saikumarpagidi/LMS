package com.lms.cdac.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MetricsDTO {
    private String name;
    private long total;
    private long completed;
    public MetricsDTO(String name, long total, long completed) {
        this.name = name;
        this.total = total;
        this.completed = completed;
    }
}
