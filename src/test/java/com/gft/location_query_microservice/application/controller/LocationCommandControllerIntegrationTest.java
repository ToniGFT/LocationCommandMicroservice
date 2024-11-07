package com.gft.location_query_microservice.application.controller;

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
public class LocationCommandControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private LocationCommandRepository locationRepository;

    // Immutable ObjectIds for test consistency
    private static final ObjectId VEHICLE_ID = new ObjectId("507f1f77bcf86cd799439011");
    private static final ObjectId ROUTE_ID = new ObjectId("507f1f77bcf86cd799439012");
    private static final ObjectId EVENT_ID_1 = new ObjectId("507f1f77bcf86cd799439013");
    private static final ObjectId EVENT_ID_2 = new ObjectId("507f1f77bcf86cd799439014");
    private static final ObjectId STOP_ID_1 = new ObjectId("507f1f77bcf86cd799439015");
    private static final ObjectId STOP_ID_2 = new ObjectId("507f1f77bcf86cd799439016");

    private LocationUpdate locationUpdate;

    @BeforeEach
    void setupDatabase() {
        locationRepository.deleteAll().block();

        locationUpdate = LocationUpdate.builder()
                .vehicleId(VEHICLE_ID)
                .timestamp(LocalDateTime.parse("2024-07-04T14:48:00"))
                .location(Coordinates.builder().latitude(40.730610).longitude(-73.935242).build())
                .speed(45.0)
                .direction(Direction.NORTH)
                .routeId(ROUTE_ID)
                .passengerCount(30)
                .status(OperationalStatus.ON_ROUTE)
                .events(List.of(
                        Event.builder()
                                .eventId(EVENT_ID_1)
                                .type(EventType.STOP_ARRIVAL)
                                .stopId(STOP_ID_1)
                                .timestamp(LocalDateTime.parse("2024-07-04T14:47:00"))
                                .build(),
                        Event.builder()
                                .eventId(EVENT_ID_2)
                                .type(EventType.STOP_DEPARTURE)
                                .stopId(STOP_ID_1)
                                .timestamp(LocalDateTime.parse("2024-07-04T14:48:00"))
                                .build()
                ))
                .build();

        locationRepository.save(locationUpdate).block();
    }

    @Test
    @DisplayName("Create LocationUpdate - Should return Created status")
    void createLocationUpdate_shouldReturnCreatedStatus() {
        // given
        LocationUpdate newLocationUpdate = LocationUpdate.builder()
                .vehicleId(VEHICLE_ID)
                .timestamp(LocalDateTime.parse("2024-07-04T15:00:00"))
                .location(Coordinates.builder().latitude(41.712776).longitude(-73.005974).build())
                .speed(50.0)
                .direction(Direction.EAST)
                .routeId(ROUTE_ID)
                .passengerCount(20)
                .status(OperationalStatus.ON_ROUTE)
                .events(List.of(
                        Event.builder()
                                .eventId(EVENT_ID_1)
                                .type(EventType.STOP_ARRIVAL)
                                .stopId(STOP_ID_2)
                                .timestamp(LocalDateTime.parse("2024-07-04T14:59:00"))
                                .build(),
                        Event.builder()
                                .eventId(EVENT_ID_2)
                                .type(EventType.STOP_DEPARTURE)
                                .stopId(STOP_ID_2)
                                .timestamp(LocalDateTime.parse("2024-07-04T15:00:00"))
                                .build()
                ))
                .build();

        // when
        webTestClient.post()
                .uri("/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newLocationUpdate)
                .exchange()

                // then
                .expectStatus().isCreated()
                .expectBody(LocationUpdate.class)
                .value(createdLocation -> {
                    assert createdLocation.getSpeed().equals(50.0);
                    assert createdLocation.getDirection() == Direction.EAST;
                    assert createdLocation.getStatus() == OperationalStatus.ON_ROUTE;
                    assert createdLocation.getEvents().size() == 2;
                });
    }

    @Test
    @DisplayName("Create LocationUpdate - Should return Bad Request for Invalid Data")
    void createLocationUpdate_shouldReturnBadRequestForInvalidData() {
        // given
        LocationUpdate invalidLocationUpdate = LocationUpdate.builder()
                .vehicleId(null)  // Invalid: Missing required field
                .timestamp(LocalDateTime.now())
                .location(Coordinates.builder().latitude(41.712776).longitude(-73.005974).build())
                .speed(70.0)
                .direction(null)  // Invalid: Null value for required field
                .routeId(ROUTE_ID)
                .passengerCount(15)
                .status(null)  // Invalid: Null value for required field
                .events(null)  // Invalid: Null list for required field
                .build();

        // when
        webTestClient.post()
                .uri("/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidLocationUpdate)
                .exchange()

                // then
                .expectStatus().isBadRequest();
    }
}
