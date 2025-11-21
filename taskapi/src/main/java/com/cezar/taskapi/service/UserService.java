package com.cezar.taskapi.service;

import com.cezar.taskapi.dto.RegisterRequest;
import com.cezar.taskapi.dto.UserResponse;
import com.cezar.taskapi.model.User;
import com.cezar.taskapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    @Transactional
    public UserResponse register(RegisterRequest request) {
        // Validar se email já existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }
        
        // Criar usuário
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        // TODO: Criptografar senha depois (por enquanto texto puro)
        user.setPassword(request.getPassword());
        
        User savedUser = userRepository.save(user);
        
        return mapToResponse(savedUser);
    }
    
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return mapToResponse(user);
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
    
    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}