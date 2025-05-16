package com.lms.cdac.entities;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "live_class_schedule")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiveClassSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Topic is required")
    @Column(nullable = false, length = 200)
    private String topic;

    @NotBlank(message = "Description is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @NotBlank(message = "Timezone is required")
    @Column(nullable = false, length = 50)
    private String timezone;

    @NotBlank(message = "Meet link is required")
    @Column(name = "meet_link", nullable = false, length = 500)
    private String liveClassLink;

    @Column(length = 100)
    private String location;

    @Column(name = "resource_center_name", length = 100)
    private String resourceCenterName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    public Integer getCourseId() {
        return (course != null) ? course.getId() : null;
    }

    /**
     * Resolves the stored timezone string to a ZoneId.
     * Falls back to system default if invalid, or attempts "Asia/Name".
     */
    @Transient
    private ZoneId resolveZoneId() {
        try {
            return ZoneId.of(this.timezone);
        } catch (DateTimeException e) {
            // try prefixing Asia/
            String tz = this.timezone.trim();
            if (!tz.contains("/")) {
                String candidate = "Asia/" + tz.substring(0,1).toUpperCase() + tz.substring(1).toLowerCase();
                try {
                    return ZoneId.of(candidate);
                } catch (DateTimeException ex) {
                    // ignore and fallback
                }
            }
            return ZoneId.systemDefault();
        }
    }

    /**
     * Computed property: is the class live now (>= start, < end)?
     */
    @Transient
    public boolean isLiveNow() {
        ZoneId zone = resolveZoneId();
        Instant startInstant = this.startTime.atZone(zone).toInstant();
        Instant endInstant   = this.endTime.atZone(zone).toInstant();
        Instant nowInstant   = Instant.now();
        return !nowInstant.isBefore(startInstant)
            && nowInstant.isBefore(endInstant);
    }

    /**
     * Formatted start time for display (dd MMM yyyy, hh:mm a)
     */
    @Transient
    public String getFormattedStartTime() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
        return this.startTime.format(fmt);
    }

    /**
     * Formatted end time for display (dd MMM yyyy, hh:mm a)
     */
    @Transient
    public String getFormattedEndTime() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
        return this.endTime.format(fmt);
    }
}