package com.example.demo.service;

import com.example.demo.entity.User;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PlannerService {
    
    private final GoalService goalService;
    private final ReminderService reminderService;
    private final ChatService chatService;

    public PlannerService(GoalService goalService, ReminderService reminderService, ChatService chatService) {
        this.goalService = goalService;
        this.reminderService = reminderService;
        this.chatService = chatService;
    }

    public String generateDailyPlan(User user) {
        // Collect goals and reminders
        var goals = goalService.getUserGoals(user.getId());
        var reminders = reminderService.getUserReminders(user.getId());

        StringBuilder context = new StringBuilder("User Goals:\n");
        goals.forEach(g -> context.append("- ").append(g.getTitle()).append(" (").append(g.getStatus()).append(")\n"));
        
        context.append("\nUser Reminders:\n");
        reminders.forEach(r -> context.append("- ").append(r.getTitle()).append("\n"));

        String systemPrompt = "You are a personal assistant. Given the user's goals and reminders, generate a friendly, structured daily plan for them.";
        return chatService.generateResponse(systemPrompt, context.toString());
    }
}
