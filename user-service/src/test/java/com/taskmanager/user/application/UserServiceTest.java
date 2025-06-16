package com.taskmanager.user.application;

import com.taskmanager.common.event.EventPublisher;
import com.taskmanager.user.application.dto.CreateUserCommand;
import com.taskmanager.user.application.dto.UserDTO;
import com.taskmanager.user.application.exception.UserAlreadyExistsException;
import com.taskmanager.user.application.exception.UserNotFoundException;
import com.taskmanager.user.domain.Role;
import com.taskmanager.user.domain.User;
import com.taskmanager.user.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private EventPublisher eventPublisher;
    
    @Mock
    private UserMapper userMapper;
    
    private UserService userService;
    
    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder, eventPublisher, userMapper);
    }
    
    @Test
    void createUser_ShouldCreateUser_WhenValidCommand() {
        // Given
        CreateUserCommand command = new CreateUserCommand(
            "test@example.com", "John", "Doe", "password123"
        );
        
        when(userRepository.existsByEmail(command.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(command.getPassword())).thenReturn("encodedPassword");
        
        User savedUser = new User(
            command.getEmail(),
            command.getFirstName(), 
            command.getLastName(),
            "encodedPassword",
            Set.of(Role.USER)
        );
        
        UserDTO expectedDTO = new UserDTO();
        expectedDTO.setEmail(command.getEmail());
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDTO(savedUser)).thenReturn(expectedDTO);
        
        // When
        UserDTO result = userService.createUser(command);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(command.getEmail());
        
        verify(userRepository).existsByEmail(command.getEmail());
        verify(passwordEncoder).encode(command.getPassword());
        verify(userRepository).save(any(User.class));
        verify(eventPublisher).publishEvent(any());
    }
    
    @Test
    void createUser_ShouldThrowException_WhenUserAlreadyExists() {
        // Given
        CreateUserCommand command = new CreateUserCommand(
            "existing@example.com", "John", "Doe", "password123"
        );
        
        when(userRepository.existsByEmail(command.getEmail())).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> userService.createUser(command))
            .isInstanceOf(UserAlreadyExistsException.class)
            .hasMessageContaining("existing@example.com");
        
        verify(userRepository).existsByEmail(command.getEmail());
        verifyNoMoreInteractions(userRepository, passwordEncoder, eventPublisher);
    }
    
    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Given
        UUID userId = UUID.randomUUID();
        User user = new User("test@example.com", "John", "Doe", "password", Set.of(Role.USER));
        UserDTO expectedDTO = new UserDTO();
        expectedDTO.setId(userId);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(expectedDTO);
        
        // When
        UserDTO result = userService.getUserById(userId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        
        verify(userRepository).findById(userId);
        verify(userMapper).toDTO(user);
    }
    
    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> userService.getUserById(userId))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining(userId.toString());
        
        verify(userRepository).findById(userId);
        verifyNoInteractions(userMapper);
    }
}