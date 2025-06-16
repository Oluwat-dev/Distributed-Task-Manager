package com.taskmanager.user.application.exception;

import com.taskmanager.common.exception.BusinessException;

import java.util.UUID;

public class UserNotFoundException extends BusinessException {
    
    public UserNotFoundException(UUID userId) {
        super("USER_NOT_FOUND", "User not found with ID: " + userId);
    }
    
    public UserNotFoundException(String email) {
        super("USER_NOT_FOUND", "User not found with email: " + email);
    }
}