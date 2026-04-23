package com.example.demo.service;

import com.example.demo.entity.Reminder;
import com.example.demo.repository.ReminderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationScheduler {

    private final ReminderRepository reminderRepository;
    private final NotificationService notificationService;

    public NotificationScheduler(ReminderRepository reminderRepository, NotificationService notificationService) {
        this.reminderRepository = reminderRepository;
        this.notificationService = notificationService;
    }

    // Run every minute
    @Scheduled(fixedRate = 60000)
    public void checkReminders() {
        List<Reminder> dueReminders = reminderRepository.findByNotifiedFalseAndDueDateBefore(LocalDateTime.now());
        
        for (Reminder reminder : dueReminders) {
            if (reminder.getUser().getDeviceToken() != null) {
                notificationService.sendPushNotification(
                        reminder.getUser().getDeviceToken(),
                        "Reminder: " + reminder.getTitle(),
                        reminder.getDescription()
                );
            }
            reminder.setNotified(true);
            reminderRepository.save(reminder);
        }
    }
}
