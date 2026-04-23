package com.example.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Standard error response format for all exceptions
 */
@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private long timestamp;
}
