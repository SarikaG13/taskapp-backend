package com.example.TaskApp.controller;

import com.example.TaskApp.entity.Subtask;
import com.example.TaskApp.entity.Task;
import com.example.TaskApp.entity.User;
import com.example.TaskApp.dto.SubtaskRequest;
import com.example.TaskApp.exceptions.BadRequestException;
import com.example.TaskApp.exceptions.NotFoundException;
import com.example.TaskApp.repo.TaskRepository;
import com.example.TaskApp.security.AuthUser;
import com.example.TaskApp.service.SubtaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/subtasks")
@CrossOrigin(
        origins = {"https://taskapp-frontend-8x0n.onrender.com"},
        allowCredentials = "true"
)
@RequiredArgsConstructor
public class SubTaskController {

    private final SubtaskService subtaskService;
    private final TaskRepository taskRepository;

    @PostMapping
    public ResponseEntity<?> createSubtask(@RequestBody SubtaskRequest request,
                                           @AuthenticationPrincipal AuthUser authUser) {
        try {
            log.info("📥 POST /api/subtasks - Payload: {}", request);

            if (authUser == null || authUser.getUser() == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
            }

            if (request.getTaskId() == null) {
                throw new BadRequestException("Task ID must not be null");
            }

            User user = authUser.getUser();
            Task task = taskRepository.findById(request.getTaskId())
                    .filter(t -> t.getUser().getId().equals(user.getId()))
                    .orElseThrow(() -> new NotFoundException("Task not found or unauthorized"));

            Subtask created = subtaskService.createSubtask(request, task);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            log.error("❌ Subtask creation failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Subtask creation failed", "error", e.getMessage()));
        }
    }

    @GetMapping("/task/{taskId}/subtasks")
    public ResponseEntity<?> getSubtasksByTaskId(@PathVariable Long taskId,
                                                 @AuthenticationPrincipal AuthUser authUser) {
        try {
            log.info("📥 GET /api/subtasks/task/{}/subtasks", taskId);

            if (authUser == null || authUser.getUser() == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
            }

            User user = authUser.getUser();
            Task task = taskRepository.findById(taskId)
                    .filter(t -> t.getUser().getId().equals(user.getId()))
                    .orElseThrow(() -> new NotFoundException("Task not found or unauthorized"));

            List<Subtask> subtasks = subtaskService.getSubtasksByTask(task);
            return ResponseEntity.ok(Map.of("data", subtasks));
        } catch (Exception e) {
            log.error("❌ Failed to fetch subtasks for taskId {}", taskId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Subtask fetch failed", "error", e.getMessage()));
        }
    }

    @PutMapping("/toggle/{id}")
    public ResponseEntity<Subtask> toggleSubtask(@PathVariable Long id,
                                                 @AuthenticationPrincipal AuthUser authUser) {
        log.info("🔄 PUT /api/subtasks/toggle/{}", id);

        if (authUser == null || authUser.getUser() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        }

        User user = authUser.getUser();
        Subtask updated = subtaskService.toggleSubtaskCompletion(id, user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubtask(@PathVariable Long id,
                                              @AuthenticationPrincipal AuthUser authUser) {
        log.info("🗑 DELETE /api/subtasks/{}", id);

        if (authUser == null || authUser.getUser() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        }

        User user = authUser.getUser();
        subtaskService.deleteSubtask(id, user);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Subtask> updateSubtask(@PathVariable Long id,
                                                 @RequestBody SubtaskRequest request,
                                                 @AuthenticationPrincipal AuthUser authUser) {
        log.info("✏️ PUT /api/subtasks/{} - Payload: {}", id, request);

        if (authUser == null || authUser.getUser() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        }

        User user = authUser.getUser();
        Subtask updated = subtaskService.updateSubtask(id, request, user);
        return ResponseEntity.ok(updated);
    }
}