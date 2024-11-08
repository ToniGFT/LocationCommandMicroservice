package com.gft.location_query_microservice.application.controller;

import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import com.gft.location_query_microservice.domain.model.valueobject.Event;
import com.gft.location_query_microservice.domain.model.valueobject.enums.Direction;
import com.gft.location_query_microservice.domain.model.valueobject.enums.EventType;
import com.gft.location_query_microservice.domain.model.valueobject.enums.OperationalStatus;
import com.gft.location_query_microservice.domain.repository.LocationCommandRepository;
import com.gft.location_query_microservice.infraestructure.model.aggregates.Vehicle;
import com.gft.location_query_microservice.infraestructure.model.entities.Driver;
import com.gft.location_query_microservice.infraestructure.model.valueobjects.Contact;
import com.gft.location_query_microservice.infraestructure.model.valueobjects.Coordinates;
import com.gft.location_query_microservice.infraestructure.model.valueobjects.MaintenanceDetails;
import com.gft.location_query_microservice.infraestructure.model.valueobjects.enums.VehicleStatus;
import com.gft.location_query_microservice.infraestructure.model.valueobjects.enums.VehicleType;
import com.gft.location_query_microservice.infraestructure.service.VehicleService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LocationCommandControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private LocationCommandRepository locationRepository;

    @MockBean
    private VehicleService vehicleService;

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

        Vehicle mockVehicle = Vehicle.builder()
                .vehicleId(VEHICLE_ID)
                .licensePlate("ABC123")
                .capacity(50)
                .currentStatus(VehicleStatus.IN_SERVICE)
                .type(VehicleType.BUS)
                .driver(new Driver(ObjectId.get(), "John Doe", new Contact("john.doe@example.com", "+1234567890")))
                .maintenanceDetails(new MaintenanceDetails("Scheduled", LocalDate.now(), ""))
                .currentLocation(new Coordinates(40.730610, -73.935242))
                .routeId(ROUTE_ID)
                .build();

        BDDMockito.given(vehicleService.getVehicleById(VEHICLE_ID.toHexString()))
                .willReturn(Mono.just(mockVehicle));

        locationUpdate = LocationUpdate.builder()
                .vehicleId(VEHICLE_ID)
                .timestamp(LocalDateTime.parse("2024-07-04T14:48:00"))
                .location(com.gft.location_query_microservice.domain.model.valueobject.Coordinates.builder().latitude(40.730610).longitude(-73.935242).build())
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
        LocationUpdate newLocationUpdate = LocationUpdate.builder()
                .vehicleId(VEHICLE_ID)
                .timestamp(LocalDateTime.parse("2024-07-04T15:00:00"))
                .location(com.gft.location_query_microservice.domain.model.valueobject.Coordinates.builder().latitude(41.712776).longitude(-73.005974).build())
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

        webTestClient.post()
                .uri("/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newLocationUpdate)
                .exchange()

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
        LocationUpdate invalidLocationUpdate = LocationUpdate.builder()
                .vehicleId(null)
                .timestamp(LocalDateTime.now())
                .location(com.gft.location_query_microservice.domain.model.valueobject.Coordinates.builder().latitude(41.712776).longitude(-73.005974).build())
                .speed(70.0)
                .direction(null)
                .routeId(ROUTE_ID)
                .passengerCount(15)
                .status(null)
                .events(null)
                .build();

        webTestClient.post()
                .uri("/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidLocationUpdate)
                .exchange()

                .expectStatus().isBadRequest();
    }
}
