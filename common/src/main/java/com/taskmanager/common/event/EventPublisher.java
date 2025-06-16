package com.taskmanager.common.event;

public interface EventPublisher {
    void publishEvent(DomainEvent event);
}