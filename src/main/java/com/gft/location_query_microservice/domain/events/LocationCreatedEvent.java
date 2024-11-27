package com.gft.location_query_microservice.domain.events;

import com.gft.location_query_microservice.domain.model.valueobject.Coordinates;
import com.gft.location_query_microservice.domain.model.valueobject.Event;
import com.gft.location_query_microservice.domain.model.valueobject.enums.Direction;
import com.gft.location_query_microservice.domain.model.valueobject.enums.OperationalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationCreatedEvent {
    private ObjectId vehicleId;
    private LocalDateTime timestamp;
    private Coordinates location;
    private Double speed;
    private Direction direction;
    private ObjectId routeId;
    private Integer passengerCount;
    private OperationalStatus status;
    private List<Event> events;
}
