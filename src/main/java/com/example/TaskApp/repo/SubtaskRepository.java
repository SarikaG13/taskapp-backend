package com.example.TaskApp.repo;

import com.example.TaskApp.entity.Subtask;
import com.example.TaskApp.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubtaskRepository extends JpaRepository<Subtask, Long> {
    List<Subtask> findByTaskId(Long taskId);
    List<Subtask> findByTask(Task task);
}