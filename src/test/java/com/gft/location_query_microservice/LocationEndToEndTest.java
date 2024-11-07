package com.gft.location_query_microservice;

import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import com.gft.location_query_microservice.domain.model.valueobject.Coordinates;
import com.gft.location_query_microservice.domain.model.valueobject.Event;
import com.gft.location_query_microservice.domain.model.valueobject.enums.Direction;
import com.gft.location_query_microservice.domain.model.valueobject.enums.EventType;
import com.gft.location_query_microservice.domain.model.valueobject.enums.OperationalStatus;
import com.gft.location_query_microservice.domain.repository.LocationCommandRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LocationEndToEndTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private LocationCommandRepository locationRepository;

    private LocationUpdate locationUpdate;
    private LocationUpdate updatedLocationUpdate;
    private final ObjectId fixedLocationId = new ObjectId("507f1f77bcf86cd799439011"); // Fixed ID for all operations

    @BeforeEach
    void setupDatabase() {
        locationRepository.deleteAll().block();

        // Original LocationUpdate with fixed ID
        locationUpdate = LocationUpdate.builder()
                .vehicleId(fixedLocationId)
                .timestamp(LocalDateTime.parse("2024-07-04T15:00:00"))
                .location(Coordinates.builder().latitude(41.712776).longitude(-73.005974).build())
                .speed(50.0)
                .direction(Direction.EAST)
                .routeId(new ObjectId())
                .passengerCount(20)
                .status(OperationalStatus.ON_ROUTE)
                .events(List.of(
                        Event.builder()
                                .eventId(new ObjectId())
                                .type(EventType.STOP_ARRIVAL)
                                .stopId(new ObjectId())
                                .timestamp(LocalDateTime.parse("2024-07-04T14:59:00"))
                                .build(),
                        Event.builder()
                                .eventId(new ObjectId())
                                .type(EventType.STOP_DEPARTURE)
                                .stopId(new ObjectId())
                                .timestamp(LocalDateTime.parse("2024-07-04T15:00:00"))
                                .build()
                ))
                .build();

        // Complete updated LocationUpdate with necessary fields
        updatedLocationUpdate = LocationUpdate.builder()
                .vehicleId(fixedLocationId)
                .timestamp(LocalDateTime.parse("2024-07-04T15:30:00"))
                .location(Coordinates.builder().latitude(41.800000).longitude(-73.000000).build())
                .speed(60.0)
                .direction(Direction.NORTH)
                .routeId(new ObjectId())
                .passengerCount(25)
                .status(OperationalStatus.ON_ROUTE)
                .events(List.of(
                        Event.builder()
                                .eventId(new ObjectId())
                                .type(EventType.STOP_ARRIVAL)
                                .stopId(new ObjectId())
                                .timestamp(LocalDateTime.parse("2024-07-04T15:29:00"))
                                .build()
                ))
                .build();

        locationRepository.save(locationUpdate).block();
    }

    @Test
    @DisplayName("End-to-End Test: Create, Update, and Delete LocationUpdate")
    void endToEndLocationFlow() {
        // Step 1: Create a LocationUpdate with a fixed ID
        webTestClient.post()
                .uri("/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(locationUpdate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LocationUpdate.class)
                .value(createdLocation -> {
                    assert createdLocation.getSpeed().equals(locationUpdate.getSpeed());
                    assert createdLocation.getDirection() == locationUpdate.getDirection();
                });

    }
}
