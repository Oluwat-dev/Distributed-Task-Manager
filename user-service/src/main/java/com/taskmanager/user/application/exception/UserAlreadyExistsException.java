package com.taskmanager.user.application.exception;

import com.taskmanager.common.exception.BusinessException;

public class UserAlreadyExistsException extends BusinessException {
    
    public UserAlreadyExistsException(String email) {
        super("USER_ALREADY_EXISTS", "User already exists with email: " + email);
    }
}