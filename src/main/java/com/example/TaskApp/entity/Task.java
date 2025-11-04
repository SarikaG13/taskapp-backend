package com.example.TaskApp.entity;

import com.example.TaskApp.enums.Priority;
import com.example.TaskApp.enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subtask> subtasks = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  Task details
    @NotBlank(message = "Title cannot be empty")
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private Boolean completed = false;

    private LocalDate dueDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Column(name = "reminder_sent")
    private Boolean reminderSent = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
}