package com.taskmanager.user.domain.event;

import com.taskmanager.common.event.DomainEvent;
import com.taskmanager.user.domain.User;

import java.util.UUID;

public class UserUpdatedEvent extends DomainEvent {
    
    private final UUID userId;
    private final String email;
    private final String fullName;
    
    public UserUpdatedEvent(User user) {
        super();
        this.userId = user.getId();
        this.email = user.getEmail();
        this.fullName = user.getFullName();
    }
    
    @Override
    public String getEventType() {
        return "USER_UPDATED";
    }
    
    @Override
    public UUID getAggregateId() {
        return userId;
    }
    
    public UUID getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
}