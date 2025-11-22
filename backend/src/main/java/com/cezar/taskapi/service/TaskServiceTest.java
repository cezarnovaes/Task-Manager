package com.cezar.taskapi.service;

import com.cezar.taskapi.dto.TaskRequest;
import com.cezar.taskapi.dto.TaskResponse;
import com.cezar.taskapi.model.Task;
import com.cezar.taskapi.model.TaskPriority;
import com.cezar.taskapi.model.TaskStatus;
import com.cezar.taskapi.model.User;
import com.cezar.taskapi.repository.TaskRepository;
import com.cezar.taskapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService Tests")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private Task task;
    private TaskRequest taskRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Teste");
        user.setEmail("teste@email.com");
        user.setPassword("senha123");

        task = new Task();
        task.setId(1L);
        task.setTitle("Tarefa Teste");
        task.setDescription("Descricao teste");
        task.setStatus(TaskStatus.PENDING);
        task.setPriority(TaskPriority.HIGH);
        task.setUser(user);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        taskRequest = new TaskRequest();
        taskRequest.setTitle("Tarefa Teste");
        taskRequest.setDescription("Descricao teste");
        taskRequest.setStatus(TaskStatus.PENDING);
        taskRequest.setPriority(TaskPriority.HIGH);
    }

    @Test
    @DisplayName("Deve criar tarefa com sucesso")
    void shouldCreateTaskSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponse response = taskService.create(1L, taskRequest);

        assertNotNull(response);
        assertEquals("Tarefa Teste", response.getTitle());
        assertEquals(TaskStatus.PENDING, response.getStatus());
        assertEquals(TaskPriority.HIGH, response.getPriority());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Deve lancar erro ao criar tarefa com usuario inexistente")
    void shouldThrowErrorWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            taskService.create(999L, taskRequest);
        });

        assertEquals("Usuario nao encontrado", exception.getMessage());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Deve listar tarefas do usuario")
    void shouldListUserTasks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(List.of(task));
        when(taskRepository.findByUserId(1L, pageable)).thenReturn(taskPage);

        Page<TaskResponse> response = taskService.findAllByUser(1L, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("Tarefa Teste", response.getContent().get(0).getTitle());
    }

    @Test
    @DisplayName("Deve buscar tarefa por ID")
    void shouldFindTaskById() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskResponse response = taskService.findById(1L, 1L);

        assertNotNull(response);
        assertEquals("Tarefa Teste", response.getTitle());
        assertEquals(1L, response.getUserId());
    }

    @Test
    @DisplayName("Deve lancar erro ao buscar tarefa de outro usuario")
    void shouldThrowErrorWhenAccessingOtherUserTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            taskService.findById(1L, 999L); // Usuario diferente
        });

        assertEquals("Acesso negado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar tarefa com sucesso")
    void shouldUpdateTaskSuccessfully() {
        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setTitle("Tarefa Atualizada");
        updateRequest.setDescription("Nova descricao");
        updateRequest.setStatus(TaskStatus.COMPLETED);
        updateRequest.setPriority(TaskPriority.LOW);

        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setTitle("Tarefa Atualizada");
        updatedTask.setDescription("Nova descricao");
        updatedTask.setStatus(TaskStatus.COMPLETED);
        updatedTask.setPriority(TaskPriority.LOW);
        updatedTask.setUser(user);
        updatedTask.setCreatedAt(LocalDateTime.now());
        updatedTask.setUpdatedAt(LocalDateTime.now());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        TaskResponse response = taskService.update(1L, 1L, updateRequest);

        assertNotNull(response);
        assertEquals("Tarefa Atualizada", response.getTitle());
        assertEquals(TaskStatus.COMPLETED, response.getStatus());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Deve deletar tarefa com sucesso")
    void shouldDeleteTaskSuccessfully() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(task);

        assertDoesNotThrow(() -> taskService.delete(1L, 1L));

        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    @DisplayName("Deve lancar erro ao deletar tarefa inexistente")
    void shouldThrowErrorWhenDeletingNonExistentTask() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            taskService.delete(999L, 1L);
        });

        assertEquals("Tarefa nao encontrada", exception.getMessage());
        verify(taskRepository, never()).delete(any(Task.class));
    }
}