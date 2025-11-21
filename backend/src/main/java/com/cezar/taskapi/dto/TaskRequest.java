package com.cezar.taskapi.dto;

import com.cezar.taskapi.model.TaskPriority;
import com.cezar.taskapi.model.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Schema(description = "Dados para criar ou atualizar tarefa")
public class TaskRequest {

    @Schema(description = "Titulo da tarefa", example = "Estudar Spring Boot")
    @NotBlank(message = "Titulo e obrigatorio")
    @Size(min = 3, max = 200, message = "Titulo deve ter entre 3 e 200 caracteres")
    private String title;

    @Schema(description = "Descricao detalhada", example = "Completar tutorial de API REST")
    @Size(max = 2000, message = "Descricao pode ter no maximo 2000 caracteres")
    private String description;

    @Schema(description = "Status da tarefa", example = "PENDING")
    private TaskStatus status;

    @Schema(description = "Prioridade da tarefa", example = "HIGH")
    private TaskPriority priority;

    @Schema(description = "Data de vencimento", example = "2024-12-31T23:59:59")
    private LocalDateTime dueDate;

    // Getters e Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
}