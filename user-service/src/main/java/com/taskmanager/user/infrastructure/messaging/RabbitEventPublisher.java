package com.taskmanager.user.infrastructure.messaging;

import com.taskmanager.common.event.DomainEvent;
import com.taskmanager.common.event.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitEventPublisher implements EventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(RabbitEventPublisher.class);
    private static final String EXCHANGE_NAME = "domain.events";
    
    private final RabbitTemplate rabbitTemplate;
    
    public RabbitEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    @Override
    public void publishEvent(DomainEvent event) {
        try {
            logger.info("Publishing event: {} for aggregate: {}", 
                event.getEventType(), event.getAggregateId());
            
            String routingKey = event.getEventType().toLowerCase().replace("_", ".");
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, routingKey, event);
            
            logger.debug("Event published successfully: {}", event.getEventId());
        } catch (Exception e) {
            logger.error("Failed to publish event: {}", event.getEventId(), e);
            throw new RuntimeException("Failed to publish domain event", e);
        }
    }
}