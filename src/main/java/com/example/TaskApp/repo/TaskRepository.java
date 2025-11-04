package com.example.TaskApp.repo;

import com.example.TaskApp.entity.Task;
import com.example.TaskApp.entity.User;
import com.example.TaskApp.enums.Priority;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserOrderByDueDateAsc(User user);

    List<Task> findByCompletedAndUser(boolean completed, User user);

    List<Task> findByPriorityAndUser(Priority priority, User user, Sort sort);

    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%')) AND t.user = :user")
    List<Task> findByTitleContainingAndUser(@Param("title") String title, @Param("user") User user);

    List<Task> findByTitleContaining(String title);

    List<Task> findByDueDateAndCompletedFalseAndReminderSentFalse(LocalDate dueDate);

    @Query("""
    SELECT t FROM Task t
    JOIN FETCH t.user
    WHERE t.dueDate <= :dueDate
      AND t.completed = false
      AND t.reminderSent = false
    """)
    List<Task> findDueSoonTasksWithUser(@Param("dueDate") LocalDate dueDate);

    List<Task> findByUserAndCompletedFalseAndDueDateLessThanEqual(User user, LocalDate dueDate);

    //  Dashboard KPIs
    long countByUser(User user);
    long countByCompletedAndUser(boolean completed, User user);
    long countByPriorityAndUser(Priority priority, User user);
}