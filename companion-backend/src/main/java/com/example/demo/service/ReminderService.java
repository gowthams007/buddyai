package com.example.demo.service;

import com.example.demo.entity.Goal;
import com.example.demo.entity.Reminder;
import com.example.demo.repository.GoalRepository;
import com.example.demo.repository.ReminderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReminderService {
    private final ReminderRepository reminderRepository;

    public ReminderService(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
    }

    public List<Reminder> getUserReminders(Long userId) {
        return reminderRepository.findByUserId(userId);
    }
    
    public Reminder createReminder(Reminder reminder) {
        return reminderRepository.save(reminder);
    }
}
