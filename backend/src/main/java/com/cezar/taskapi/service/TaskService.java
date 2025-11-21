package com.cezar.taskapi.service;

import com.cezar.taskapi.dto.TaskRequest;
import com.cezar.taskapi.dto.TaskResponse;
import com.cezar.taskapi.model.Task;
import com.cezar.taskapi.model.TaskPriority;
import com.cezar.taskapi.model.TaskStatus;
import com.cezar.taskapi.model.User;
import com.cezar.taskapi.repository.TaskRepository;
import com.cezar.taskapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public TaskResponse create(Long userId, TaskRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus() != null ? request.getStatus() : TaskStatus.PENDING);
        task.setPriority(request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM);
        task.setDueDate(request.getDueDate());
        task.setUser(user);
        
        Task savedTask = taskRepository.save(task);
        return mapToResponse(savedTask);
    }
    
    public Page<TaskResponse> findAllByUser(Long userId, Pageable pageable) {
        return taskRepository.findByUserId(userId, pageable)
                .map(this::mapToResponse);
    }
    
    public Page<TaskResponse> findByUserAndStatus(Long userId, TaskStatus status, Pageable pageable) {
        return taskRepository.findByUserIdAndStatus(userId, status, pageable)
                .map(this::mapToResponse);
    }
    
    public TaskResponse findById(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
        
        // Validar se a tarefa pertence ao usuário
        if (!task.getUser().getId().equals(userId)) {
            throw new RuntimeException("Acesso negado");
        }
        
        return mapToResponse(task);
    }
    
    @Transactional
    public TaskResponse update(Long taskId, Long userId, TaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
        
        // Validar se a tarefa pertence ao usuário
        if (!task.getUser().getId().equals(userId)) {
            throw new RuntimeException("Acesso negado");
        }
        
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        task.setDueDate(request.getDueDate());
        
        Task updatedTask = taskRepository.save(task);
        return mapToResponse(updatedTask);
    }
    
    @Transactional
    public void delete(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
        
        // Validar se a tarefa pertence ao usuário
        if (!task.getUser().getId().equals(userId)) {
            throw new RuntimeException("Acesso negado");
        }
        
        taskRepository.delete(task);
    }
    
    private TaskResponse mapToResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getUser().getId()
        );
    }
}