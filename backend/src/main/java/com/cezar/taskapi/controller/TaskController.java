package com.cezar.taskapi.controller;

import com.cezar.taskapi.dto.TaskRequest;
import com.cezar.taskapi.dto.TaskResponse;
import com.cezar.taskapi.model.TaskStatus;
import com.cezar.taskapi.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    
    private final TaskService taskService;
    
    // TODO: Depois vamos pegar o userId do token JWT
    // Por enquanto, vamos passar como parâmetro
    
    @PostMapping
    public ResponseEntity<TaskResponse> create(
            @RequestParam Long userId,
            @Valid @RequestBody TaskRequest request) {
        TaskResponse response = taskService.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getAll(
            @RequestParam Long userId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("ASC") 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<TaskResponse> tasks = status != null
                ? taskService.findByUserAndStatus(userId, status, pageable)
                : taskService.findAllByUser(userId, pageable);
        
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getById(
            @PathVariable Long id,
            @RequestParam Long userId) {
        TaskResponse response = taskService.findById(id, userId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(
            @PathVariable Long id,
            @RequestParam Long userId,
            @Valid @RequestBody TaskRequest request) {
        TaskResponse response = taskService.update(id, userId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestParam Long userId) {
        taskService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}