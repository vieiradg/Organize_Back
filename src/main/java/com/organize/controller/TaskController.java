package com.organize.controller;

import com.organize.model.Task;
import com.organize.model.User;
import com.organize.service.TaskService;
import com.organize.service.UserService; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Task>> getTasks(@AuthenticationPrincipal User authenticatedUser) {
        List<Task> tasks = taskService.getTasksByUser(authenticatedUser);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task, @AuthenticationPrincipal User authenticatedUser) {
        task.setUser(authenticatedUser);
        Task createdTask = taskService.saveTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task taskDetails, @AuthenticationPrincipal User authenticatedUser) {
        Optional<Task> existingTask = taskService.getTaskByIdAndUser(id, authenticatedUser);
        if (existingTask.isPresent()) {
            Task task = existingTask.get();
            task.setTitle(taskDetails.getTitle());
            task.setDescription(taskDetails.getDescription());
            task.setCompleted(taskDetails.isCompleted());
            Task updatedTask = taskService.saveTask(task);
            return ResponseEntity.ok(updatedTask);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, @AuthenticationPrincipal User authenticatedUser) {
        Optional<Task> existingTask = taskService.getTaskByIdAndUser(id, authenticatedUser);
        if (existingTask.isPresent()) {
            taskService.deleteTask(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
