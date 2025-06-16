package com.taskmanager.user.domain.event;

import com.taskmanager.common.event.DomainEvent;
import com.taskmanager.user.domain.User;

import java.util.UUID;

public class UserCreatedEvent extends DomainEvent {
    
    private final UUID userId;
    private final String email;
    private final String fullName;
    
    public UserCreatedEvent(User user) {
        super();
        this.userId = user.getId();
        this.email = user.getEmail();
        this.fullName = user.getFullName();
    }
    
    @Override
    public String getEventType() {
        return "USER_CREATED";
    }
    
    @Override
    public UUID getAggregateId() {
        return userId;
    }
    
    public UUID getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
}