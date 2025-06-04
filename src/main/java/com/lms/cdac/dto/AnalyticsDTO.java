package com.lms.cdac.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * AnalyticsDTO:
 *   - category: analytics variable name (like "ActiveUsers" or "Course-1")
 *   - value: count or average (Long or Double)
 */
@Getter
@Setter
public class AnalyticsDTO {

    private String category;
    private Number value;  // Can hold Long or Double

    public AnalyticsDTO(String category, Long value) {
        this.category = category;
        this.value = value;
    }

    public AnalyticsDTO(String category, Double value) {
        this.category = category;
        this.value = value;
    }

    public AnalyticsDTO() { }
}
