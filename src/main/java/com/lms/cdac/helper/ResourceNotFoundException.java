package com.lms.cdac.helper;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    // Constructor with custom message
    public ResourceNotFoundException(String message) {
        super(message);
    }

    // Default constructor with generic message
    public ResourceNotFoundException() {
        super("Resource not found");
    }
}
