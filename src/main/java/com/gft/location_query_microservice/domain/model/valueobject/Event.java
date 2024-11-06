package com.gft.location_query_microservice.domain.model.valueobject;

import com.gft.location_query_microservice.domain.model.valueobject.enums.EventType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class Event {

    @NotNull(message = "Event ID cannot be null")
    private ObjectId eventId;

    @NotNull(message = "Event type cannot be null")
    private EventType type;

    @NotNull(message = "Stop ID cannot be null")
    private ObjectId stopId;

    @NotNull(message = "Timestamp cannot be null")
    @PastOrPresent(message = "Timestamp must be in the past or present")
    private LocalDateTime timestamp;

    @Size(max = 255, message = "Details must not exceed 255 characters")
    private String details;
}
