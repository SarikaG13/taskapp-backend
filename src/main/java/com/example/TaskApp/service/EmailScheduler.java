package com.example.TaskApp.service;

import com.example.TaskApp.entity.Task;
import com.example.TaskApp.repo.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailScheduler {

    private final TaskRepository taskRepository;
    private final EmailService emailService;

    private final List<TaskReminderStatus> lastRunStatus = new ArrayList<>();

    private static final String FRONTEND_URL = "https://taskapp-frontend-8x0n.onrender.com";

    @Scheduled(cron = "0 0 9 * * *") //  Runs daily at 9:00 AM
    public void sendDueTaskReminders() {
        log.info("📅 Running scheduled task reminder check...");
        lastRunStatus.clear();

        LocalDate twoDaysFromNow = LocalDate.now().plusDays(2);
        List<Task> tasksDueSoon = taskRepository.findDueSoonTasksWithUser(twoDaysFromNow);

        for (Task task : tasksDueSoon) {
            TaskReminderStatus status = new TaskReminderStatus(task.getId());

            if (task.getUser() == null) {
                log.warn("⚠️ Skipping task {} — no user linked", task.getId());
                status.setSent(false);
                status.setReason("No user linked");
                lastRunStatus.add(status);
                continue;
            }

            String userEmail = task.getUser().getEmail();
            String userName = task.getUser().getName();

            if (userEmail == null || userEmail.isBlank()) {
                log.warn("⚠️ Skipping task {} — user email is missing", task.getId());
                status.setSent(false);
                status.setReason("User email is missing");
                lastRunStatus.add(status);
                continue;
            }

            String subject = "⏰ REMINDER: Task Due Soon - " + task.getTitle();
            String body = String.format("""
                <h2>Hi %s,</h2>
                <p>Your task <strong>%s</strong> is due on <strong>%s</strong>.</p>
                <p><a href='%s/tasks'>Click here to view your task</a></p>
                <p>Stay focused and keep crushing it!<br>– TaskApp</p>
            """, userName != null ? userName : "User", task.getTitle(), task.getDueDate(), FRONTEND_URL);

            emailService.sendReminderEmail(userEmail, subject, body);

            task.setReminderSent(true);
            taskRepository.save(task);

            status.setSent(true);
            status.setReason("Email sent");
            lastRunStatus.add(status);
        }

        log.info("✅ Task reminder check complete. Sent {} emails.",
                lastRunStatus.stream().filter(TaskReminderStatus::isSent).count());
        log.info("📊 Found {} tasks due on or before {}", tasksDueSoon.size(), twoDaysFromNow);
    }

    public List<TaskReminderStatus> getLastRunStatus() {
        return lastRunStatus;
    }

    public static class TaskReminderStatus {
        private final Long taskId;
        private boolean sent;
        private String reason;

        public TaskReminderStatus(Long taskId) {
            this.taskId = taskId;
        }

        public Long getTaskId() {
            return taskId;
        }

        public boolean isSent() {
            return sent;
        }

        public void setSent(boolean sent) {
            this.sent = sent;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}