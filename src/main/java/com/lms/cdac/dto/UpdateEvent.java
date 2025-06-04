package com.lms.cdac.dto;

import java.time.LocalDateTime;

/**
 * DTO for Server-Sent Event payloads.
 */
public class UpdateEvent {
    private String type;           // e.g., "REFRESH", "NEW_REGISTRATION", etc.
    private LocalDateTime timestamp;
    private Object payload;        // Optional additional data

    public UpdateEvent() {
        // Default constructor
    }

    public UpdateEvent(String type, LocalDateTime timestamp, Object payload) {
        this.type = type;
        this.timestamp = timestamp;
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "UpdateEvent{" +
                "type='" + type + '\'' +
                ", timestamp=" + timestamp +
                ", payload=" + payload +
                '}';
    }
}
