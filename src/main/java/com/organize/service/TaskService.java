package com.organize.service;

import com.organize.model.Task;
import com.organize.model.User;
import com.organize.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public List<Task> getTasksByUser(User user) {
        return taskRepository.findByUser(user);
    }

    public Optional<Task> getTaskByIdAndUser(Long id, User user) {
        return taskRepository.findById(id).filter(task -> task.getUser().equals(user));
    }

    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
