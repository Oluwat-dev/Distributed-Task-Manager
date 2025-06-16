package com.taskmanager.user.application;

import com.taskmanager.common.event.EventPublisher;
import com.taskmanager.user.application.dto.CreateUserCommand;
import com.taskmanager.user.application.dto.UpdateUserCommand;
import com.taskmanager.user.application.dto.UserDTO;
import com.taskmanager.user.application.exception.UserNotFoundException;
import com.taskmanager.user.application.exception.UserAlreadyExistsException;
import com.taskmanager.user.domain.Role;
import com.taskmanager.user.domain.User;
import com.taskmanager.user.infrastructure.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EventPublisher eventPublisher;
    private final UserMapper userMapper;
    
    public UserService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder,
                      EventPublisher eventPublisher,
                      UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
        this.userMapper = userMapper;
    }
    
    public UserDTO createUser(CreateUserCommand command) {
        logger.info("Creating user with email: {}", command.getEmail());
        
        if (userRepository.existsByEmail(command.getEmail())) {
            throw new UserAlreadyExistsException(command.getEmail());
        }
        
        String encodedPassword = passwordEncoder.encode(command.getPassword());
        User user = new User(
            command.getEmail(),
            command.getFirstName(),
            command.getLastName(),
            encodedPassword,
            Set.of(Role.USER)
        );
        
        User savedUser = userRepository.save(user);
        
        // Publish domain events
        savedUser.getDomainEvents().forEach(eventPublisher::publishEvent);
        savedUser.clearDomainEvents();
        
        logger.info("User created successfully with ID: {}", savedUser.getId());
        return userMapper.toDTO(savedUser);
    }
    
    @Cacheable(value = "users", key = "#id")
    @Transactional(readOnly = true)
    public UserDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toDTO(user);
    }
    
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException(email));
        return userMapper.toDTO(user);
    }
    
    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
            .map(userMapper::toDTO);
    }
    
    @CacheEvict(value = "users", key = "#id")
    public UserDTO updateUser(UUID id, UpdateUserCommand command) {
        logger.info("Updating user with ID: {}", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        
        user.updateProfile(command.getFirstName(), command.getLastName());
        User savedUser = userRepository.save(user);
        
        // Publish domain events
        savedUser.getDomainEvents().forEach(eventPublisher::publishEvent);
        savedUser.clearDomainEvents();
        
        logger.info("User updated successfully with ID: {}", savedUser.getId());
        return userMapper.toDTO(savedUser);
    }
    
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(UUID id) {
        logger.info("Deleting user with ID: {}", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        
        user.deactivate();
        User savedUser = userRepository.save(user);
        
        // Publish domain events
        savedUser.getDomainEvents().forEach(eventPublisher::publishEvent);
        savedUser.clearDomainEvents();
        
        logger.info("User deleted successfully with ID: {}", id);
    }
}