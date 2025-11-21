package com.cezar.taskapi.controller;

import com.cezar.taskapi.dto.TaskRequest;
import com.cezar.taskapi.dto.TaskResponse;
import com.cezar.taskapi.model.TaskStatus;
import com.cezar.taskapi.model.User;
import com.cezar.taskapi.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TaskRequest request) {
        TaskResponse response = taskService.create(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getAll(
            @AuthenticationPrincipal User user,
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
                ? taskService.findByUserAndStatus(user.getId(), status, pageable)
                : taskService.findAllByUser(user.getId(), pageable);

        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getById(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        TaskResponse response = taskService.findById(id, user.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {
        TaskResponse response = taskService.update(id, user.getId(), request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        taskService.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
