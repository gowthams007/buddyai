package com.example.demo.controller;

import com.example.demo.dto.ReminderDto;
import com.example.demo.entity.Reminder;
import com.example.demo.entity.User;
import com.example.demo.service.ReminderService;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {

    private final ReminderService reminderService;
    private final UserService userService;

    public ReminderController(ReminderService reminderService, UserService userService) {
        this.reminderService = reminderService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<ReminderDto>> getReminders(@RequestParam Long userId) {
        List<Reminder> reminders = reminderService.getUserReminders(userId);
        List<ReminderDto> dtos = reminders.stream().map(r -> {
            ReminderDto dto = new ReminderDto();
            dto.setId(r.getId());
            dto.setUserId(r.getUser().getId());
            dto.setTitle(r.getTitle());
            dto.setDescription(r.getDescription());
            dto.setDueDate(r.getDueDate());
            dto.setCompleted(r.isCompleted());
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<ReminderDto> createReminder(@RequestBody ReminderDto request) {
        User user = userService.getOrCreateUser(request.getUserId());
        Reminder reminder = Reminder.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .completed(false)
                .notified(false)
                .build();
        Reminder saved = reminderService.createReminder(reminder);
        request.setId(saved.getId());
        return ResponseEntity.ok(request);
    }
}
