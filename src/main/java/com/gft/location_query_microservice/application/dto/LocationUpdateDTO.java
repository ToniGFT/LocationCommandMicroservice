package com.gft.location_query_microservice.application.dto;

import com.gft.location_query_microservice.domain.model.valueobject.Coordinates;
import com.gft.location_query_microservice.domain.model.valueobject.Event;
import com.gft.location_query_microservice.domain.model.valueobject.enums.Direction;
import com.gft.location_query_microservice.domain.model.valueobject.enums.OperationalStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class LocationUpdateDTO {

    @NotNull(message = "Timestamp cannot be null")
    @PastOrPresent(message = "Timestamp must be in the past or present")
    private LocalDateTime timestamp;

    @NotNull(message = "Location coordinates cannot be null")
    @Valid
    private Coordinates location;

    @NotNull(message = "Speed cannot be null")
    @PositiveOrZero(message = "Speed must be zero or positive")
    private Double speed;

    @NotNull(message = "Direction cannot be null")
    private Direction direction;

    @NotNull(message = "Route ID cannot be null")
    private ObjectId routeId;

    @NotNull(message = "Passenger count cannot be null")
    @Min(value = 0, message = "Passenger count must be zero or positive")
    private Integer passengerCount;

    @NotNull(message = "Operational status cannot be null")
    private OperationalStatus status;

    @NotEmpty(message = "Events list cannot be empty")
    @Valid
    private List<Event> events;
}
