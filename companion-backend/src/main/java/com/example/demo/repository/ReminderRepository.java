package com.example.demo.repository;

import com.example.demo.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByUserId(Long userId);
    List<Reminder> findByNotifiedFalseAndDueDateBefore(LocalDateTime time);
}
