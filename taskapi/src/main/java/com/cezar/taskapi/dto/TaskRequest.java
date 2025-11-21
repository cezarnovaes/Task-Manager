package com.cezar.taskapi.dto;

import com.cezar.taskapi.model.TaskPriority;
import com.cezar.taskapi.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskRequest {
    
    @NotBlank(message = "Título é obrigatório")
    @Size(min = 3, max = 200, message = "Título deve ter entre 3 e 200 caracteres")
    private String title;
    
    @Size(max = 2000, message = "Descrição pode ter no máximo 2000 caracteres")
    private String description;
    
    private TaskStatus status;
    
    private TaskPriority priority;
    
    private LocalDateTime dueDate;
}