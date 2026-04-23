package com.example.demo.service;

import com.example.demo.entity.Goal;
import com.example.demo.repository.GoalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoalService {
    private final GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public List<Goal> getUserGoals(Long userId) {
        return goalRepository.findByUserId(userId);
    }

    public Goal createGoal(Goal goal) {
        return goalRepository.save(goal);
    }
}
