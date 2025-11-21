package com.cezar.taskapi.controller;

import com.cezar.taskapi.dto.TaskRequest;
import com.cezar.taskapi.dto.TaskResponse;
import com.cezar.taskapi.model.TaskStatus;
import com.cezar.taskapi.model.User;
import com.cezar.taskapi.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Tarefas", description = "CRUD completo de tarefas")
@SecurityRequirement(name = "Bearer Authentication")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @Operation(summary = "Criar tarefa", description = "Cria uma nova tarefa para o usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "403", description = "Token invalido ou ausente")
    })
    public ResponseEntity<TaskResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TaskRequest request) {
        TaskResponse response = taskService.create(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar tarefas", description = "Lista todas as tarefas do usuario com paginacao e filtros")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tarefas"),
            @ApiResponse(responseCode = "403", description = "Token invalido ou ausente")
    })
    public ResponseEntity<Page<TaskResponse>> getAll(
            @AuthenticationPrincipal User user,
            @Parameter(description = "Filtrar por status") @RequestParam(required = false) TaskStatus status,
            @Parameter(description = "Numero da pagina (comeca em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Itens por pagina") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenacao") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Direcao: ASC ou DESC") @RequestParam(defaultValue = "DESC") String direction) {

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
    @Operation(summary = "Buscar tarefa", description = "Busca uma tarefa pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa encontrada",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Tarefa nao encontrada ou acesso negado"),
            @ApiResponse(responseCode = "403", description = "Token invalido ou ausente")
    })
    public ResponseEntity<TaskResponse> getById(
            @AuthenticationPrincipal User user,
            @Parameter(description = "ID da tarefa") @PathVariable Long id) {
        TaskResponse response = taskService.findById(id, user.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar tarefa", description = "Atualiza uma tarefa existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa atualizada",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos ou tarefa nao encontrada"),
            @ApiResponse(responseCode = "403", description = "Token invalido ou ausente")
    })
    public ResponseEntity<TaskResponse> update(
            @AuthenticationPrincipal User user,
            @Parameter(description = "ID da tarefa") @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {
        TaskResponse response = taskService.update(id, user.getId(), request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar tarefa", description = "Remove uma tarefa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarefa deletada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Tarefa nao encontrada ou acesso negado"),
            @ApiResponse(responseCode = "403", description = "Token invalido ou ausente")
    })
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User user,
            @Parameter(description = "ID da tarefa") @PathVariable Long id) {
        taskService.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}