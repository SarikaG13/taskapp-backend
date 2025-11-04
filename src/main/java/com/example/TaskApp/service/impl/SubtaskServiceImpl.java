package com.example.TaskApp.service.impl;

import com.example.TaskApp.dto.SubtaskRequest;
import com.example.TaskApp.entity.Subtask;
import com.example.TaskApp.entity.Task;
import com.example.TaskApp.entity.User;
import com.example.TaskApp.exceptions.NotFoundException;
import com.example.TaskApp.repo.SubtaskRepository;
import com.example.TaskApp.repo.TaskRepository;
import com.example.TaskApp.service.SubtaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubtaskServiceImpl implements SubtaskService {

    private final SubtaskRepository subtaskRepo;
    private final TaskRepository taskRepo;

    @Override
    public Subtask createSubtask(SubtaskRequest request, Task task) {
        log.info("📥 Creating subtask for task ID: {}", task.getId());

        Subtask subtask = Subtask.builder()
                .title(request.getTitle())
                .completed(false)
                .task(task)
                .build();

        return subtaskRepo.save(subtask);
    }

    @Override
    public List<Subtask> getSubtasksByTask(Task task) {
        log.info("📤 Fetching subtasks for task ID: {}", task.getId());
        return subtaskRepo.findByTask(task);
    }

    @Override
    public Subtask toggleSubtaskCompletion(Long subtaskId, User user) {
        log.info("🔄 Toggling subtask ID: {}", subtaskId);

        Subtask subtask = subtaskRepo.findById(subtaskId)
                .filter(s -> s.getTask().getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new NotFoundException("Subtask not found or unauthorized"));

        subtask.setCompleted(!subtask.isCompleted());
        return subtaskRepo.save(subtask);
    }

    @Override
    public void deleteSubtask(Long subtaskId, User user) {
        log.info("🗑 Deleting subtask ID: {}", subtaskId);

        Subtask subtask = subtaskRepo.findById(subtaskId)
                .filter(s -> s.getTask().getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new NotFoundException("Subtask not found or unauthorized"));

        subtaskRepo.delete(subtask);
    }

    @Override
    public Subtask updateSubtask(Long subtaskId, SubtaskRequest request, User user) {
        log.info("✏️ Updating subtask ID: {}", subtaskId);

        Subtask subtask = subtaskRepo.findById(subtaskId)
                .filter(s -> s.getTask().getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new NotFoundException("Subtask not found or unauthorized"));

        if (request.getTitle() != null) subtask.setTitle(request.getTitle());
        if (request.getCompleted() != null) subtask.setCompleted(request.getCompleted());

        return subtaskRepo.save(subtask);
    }
}