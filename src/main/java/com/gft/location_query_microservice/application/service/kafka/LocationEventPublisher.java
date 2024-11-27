package com.gft.location_query_microservice.application.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gft.location_query_microservice.domain.events.LocationCreatedEvent;
import com.gft.location_query_microservice.domain.events.LocationDeletedEvent;
import com.gft.location_query_microservice.domain.events.LocationUpdatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.util.HashMap;
import java.util.Map;

@Service
public class LocationEventPublisher {

    private final KafkaSender<String, String> kafkaSender;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topics.location-events}")
    private String locationEventsTopic;

    public LocationEventPublisher(KafkaSender<String, String> kafkaSender, ObjectMapper objectMapper) {
        this.kafkaSender = kafkaSender;
        this.objectMapper = objectMapper;
    }

    public Mono<Void> publishLocationCreatedEvent(LocationCreatedEvent event) {
        return publishEvent("LOCATION_CREATED", event);
    }

    public Mono<Void> publishLocationUpdatedEvent(LocationUpdatedEvent event) {
        return publishEvent("LOCATION_UPDATED", event);
    }

    public Mono<Void> publishLocationDeletedEvent(LocationDeletedEvent event) {
        return publishEvent("LOCATION_DELETED", event);
    }

    private <T> Mono<Void> publishEvent(String eventType, T event) {
        try {
            Map<String, Object> messagePayload = new HashMap<>();
            messagePayload.put("type", eventType);
            messagePayload.putAll(objectMapper.convertValue(event, Map.class));

            String message = objectMapper.writeValueAsString(messagePayload);
            return kafkaSender.send(Mono.just(
                    SenderRecord.create(locationEventsTopic, null, System.currentTimeMillis(), null, message, null)
            )).then();
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Failed to publish event: " + eventType, e));
        }
    }
}
