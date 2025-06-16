package com.taskmanager.user.domain.event;

import com.taskmanager.common.event.DomainEvent;
import com.taskmanager.user.domain.User;

import java.util.UUID;

public class UserDeletedEvent extends DomainEvent {
    
    private final UUID userId;
    private final String email;
    
    public UserDeletedEvent(User user) {
        super();
        this.userId = user.getId();
        this.email = user.getEmail();
    }
    
    @Override
    public String getEventType() {
        return "USER_DELETED";
    }
    
    @Override
    public UUID getAggregateId() {
        return userId;
    }
    
    public UUID getUserId() { return userId; }
    public String getEmail() { return email; }
}