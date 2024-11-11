package com.gft.location_query_microservice.application.controller;

import com.gft.location_query_microservice.application.dto.LocationUpdateDTO;
import com.gft.location_query_microservice.application.service.LocationCommandService;
import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import com.gft.location_query_microservice.domain.model.valueobject.Coordinates;
import com.gft.location_query_microservice.domain.model.valueobject.Event;
import com.gft.location_query_microservice.domain.model.valueobject.enums.Direction;
import com.gft.location_query_microservice.domain.model.valueobject.enums.EventType;
import com.gft.location_query_microservice.domain.model.valueobject.enums.OperationalStatus;
import com.gft.location_query_microservice.domain.repository.LocationCommandRepository;
import com.gft.location_query_microservice.infraestructure.model.aggregates.Vehicle;
import com.gft.location_query_microservice.infraestructure.model.valueobjects.enums.VehicleStatus;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LocationCommandControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private LocationCommandService locationCommandService;

    @MockBean
    private LocationCommandRepository locationRepository;

    private LocationUpdate sampleLocationUpdate;
    private LocationUpdateDTO sampleLocationUpdateDTO;


    @BeforeEach
    void setup() {
        sampleLocationUpdate = LocationUpdate.builder()
                .vehicleId(new ObjectId("672b1343bb9d3b2fdd48ac14"))
                .timestamp(LocalDateTime.now().minusMinutes(5))
                .location(new Coordinates(12.34, 56.78))
                .speed(50.0)
                .direction(Direction.NORTH)
                .routeId(new ObjectId("672b1343bb9d3b2fdd48ac14"))
                .passengerCount(10)
                .status(OperationalStatus.ON_ROUTE)
                .events(Collections.singletonList(
                        Event.builder()
                                .eventId(new ObjectId("672b1343bb9d3b2fdd48ac14"))
                                .type(EventType.STOP_ARRIVAL)
                                .stopId(new ObjectId("672b1343bb9d3b2fdd48ac14"))
                                .timestamp(LocalDateTime.now().minusMinutes(10))
                                .details("Arrived at stop")
                                .build()
                ))
                .build();

        sampleLocationUpdateDTO = LocationUpdateDTO.builder()
                .timestamp(LocalDateTime.now())
                .location(new Coordinates(12.34, 57.00))
                .speed(60.0)
                .direction(Direction.EAST)
                .routeId(new ObjectId("672b1343bb9d3b2fdd48ac14"))
                .passengerCount(15)
                .status(OperationalStatus.STOPPED)
                .events(Collections.singletonList(
                        Event.builder()
                                .eventId(new ObjectId("672b1343bb9d3b2fdd48ac14"))
                                .type(EventType.STOP_DEPARTURE)
                                .stopId(new ObjectId("672b1343bb9d3b2fdd48ac14"))
                                .timestamp(LocalDateTime.now().minusMinutes(5))
                                .details("Departed from stop")
                                .build()
                ))
                .build();

        Vehicle mockVehicle = Vehicle.builder()
                .vehicleId(new ObjectId("672b1343bb9d3b2fdd48ac14"))
                .licensePlate("ABC123")
                .capacity(50)
                .currentStatus(VehicleStatus.IN_SERVICE)
                .routeId(new ObjectId("672b1343bb9d3b2fdd48ac14"))
                .build();

    }

    @Test
    public void testCreateLocationUpdate() {

        when(locationCommandService.saveLocationUpdate(any(LocationUpdate.class)))
                .thenReturn(Mono.just(sampleLocationUpdate));

        webTestClient.post()
                .uri("/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sampleLocationUpdate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.speed").isEqualTo(50.0)
                .jsonPath("$.passengerCount").isEqualTo(10);
    }

    @Test
    @DisplayName("Create LocationUpdate - Should return Bad Request for Invalid Data")
    void createLocationUpdate_shouldReturnBadRequestForInvalidData() {
        LocationUpdate invalidLocationUpdate = LocationUpdate.builder()
                .vehicleId(new ObjectId("672b1343bb9d3b2fdd48ac14"))
                .timestamp(LocalDateTime.now())
                .location(new Coordinates(41.712776, -73.005974))
                .speed(70.0)
                .direction(null)
                .routeId(new ObjectId("672b1343bb9d3b2fdd48ac14"))
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

    @Test
    public void testUpdateLocationUpdate() {

        when(locationCommandService.updateLocationUpdate(any(ObjectId.class),any(LocationUpdateDTO.class)))
                .thenReturn(Mono.just(sampleLocationUpdate));

        webTestClient.put().uri("/locations/{id}", sampleLocationUpdate.getVehicleId().toHexString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sampleLocationUpdateDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LocationUpdate.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals(sampleLocationUpdate.getSpeed(), response.getSpeed());
                    assertEquals(sampleLocationUpdate.getStatus(), response.getStatus());
                });
    }

    @Test
    public void testDeleteLocationUpdate() {
        when(locationCommandService.deleteLocationUpdate(eq(sampleLocationUpdate.getVehicleId())))
                .thenReturn(Mono.empty());

        webTestClient.delete().uri("/locations/{id}",sampleLocationUpdate.getVehicleId().toHexString() )
                .exchange()
                .expectStatus().isNoContent();
    }
}
