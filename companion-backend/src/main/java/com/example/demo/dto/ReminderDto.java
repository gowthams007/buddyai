package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Request DTO for creating/updating reminders
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReminderDto {
    
    private Long id;
    
    @NotNull(message = "User ID cannot be null")
    private Long userId;
    
    @NotBlank(message = "Reminder title cannot be empty")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    @NotNull(message = "Due date cannot be empty")
    private LocalDateTime dueDate;
    
    private String frequency;
    
    private Boolean completed;
}
