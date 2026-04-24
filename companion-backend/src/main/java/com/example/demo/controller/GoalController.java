package com.example.demo.controller;

import com.example.demo.dto.GoalDto;
import com.example.demo.entity.Goal;
import com.example.demo.entity.User;
import com.example.demo.service.GoalService;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/goals")
public class GoalController {

    private final GoalService goalService;
    private final UserService userService;

    public GoalController(GoalService goalService, UserService userService) {
        this.goalService = goalService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<GoalDto>> getGoals(@RequestParam Long userId) {
        List<Goal> goals = goalService.getUserGoals(userId);
        List<GoalDto> dtos = goals.stream().map(g -> {
            GoalDto dto = new GoalDto();
            dto.setId(g.getId());
            dto.setUserId(g.getUser().getId());
            dto.setTitle(g.getTitle());
            dto.setDescription(g.getDescription());
            dto.setTargetDate(g.getTargetDate());
            dto.setStatus(g.getStatus());
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<GoalDto> createGoal(@RequestBody GoalDto request) {
        User user = userService.getOrCreateUser(request.getUserId());
        Goal goal = Goal.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .targetDate(request.getTargetDate())
                .status(request.getStatus() != null ? request.getStatus() : "PENDING")
                .build();
        Goal saved = goalService.createGoal(goal);
        request.setId(saved.getId());
        return ResponseEntity.ok(request);
    }
}
