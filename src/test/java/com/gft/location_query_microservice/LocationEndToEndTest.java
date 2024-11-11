package com.gft.location_query_microservice;

import com.gft.location_query_microservice.application.dto.LocationUpdateDTO;
import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import com.gft.location_query_microservice.domain.model.valueobject.Coordinates;
import com.gft.location_query_microservice.domain.model.valueobject.Event;
import com.gft.location_query_microservice.domain.model.valueobject.enums.Direction;
import com.gft.location_query_microservice.domain.model.valueobject.enums.EventType;
import com.gft.location_query_microservice.domain.model.valueobject.enums.OperationalStatus;
import com.gft.location_query_microservice.domain.repository.LocationCommandRepository;
import com.gft.location_query_microservice.infraestructure.model.aggregates.Vehicle;
import com.gft.location_query_microservice.infraestructure.model.entities.Driver;
import com.gft.location_query_microservice.infraestructure.model.valueobjects.Contact;
import com.gft.location_query_microservice.infraestructure.model.valueobjects.MaintenanceDetails;
import com.gft.location_query_microservice.infraestructure.model.valueobjects.VehicleCoordinates;
import com.gft.location_query_microservice.infraestructure.model.valueobjects.enums.VehicleStatus;
import com.gft.location_query_microservice.infraestructure.model.valueobjects.enums.VehicleType;
import com.gft.location_query_microservice.infraestructure.service.VehicleService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
@AutoConfigureWebTestClient
public class LocationEndToEndTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private LocationCommandRepository locationCommandRepository;

    @MockBean
    private VehicleService vehicleService;

    private LocationUpdate sampleLocationUpdate;
    private LocationUpdateDTO sampleLocationUpdateDTO;
    private Vehicle mockVehicle;

    private final ObjectId fixedLocationId = new ObjectId("507f1f77bcf86cd799439011");

    @BeforeEach
    public void setUp() {
        sampleLocationUpdate = LocationUpdate.builder()
                .vehicleId(fixedLocationId)
                .timestamp(LocalDateTime.now().minusMinutes(5))
                .location(new Coordinates(12.34, 56.78))
                .speed(50.0)
                .direction(Direction.NORTH)
                .routeId(new ObjectId())
                .passengerCount(10)
                .status(OperationalStatus.ON_ROUTE)
                .events(Collections.singletonList(
                        Event.builder()
                                .eventId(new ObjectId())
                                .type(EventType.STOP_ARRIVAL)
                                .stopId(new ObjectId())
                                .timestamp(LocalDateTime.now().minusMinutes(10))
                                .details("Arrived at stop")
                                .build()
                ))
                .build();

        sampleLocationUpdateDTO = LocationUpdateDTO.builder()
                .routeId(fixedLocationId)
                .timestamp(LocalDateTime.now())
                .location(new Coordinates(12.34, 57.00))
                .speed(60.0)
                .direction(Direction.EAST)
                .routeId(new ObjectId())
                .passengerCount(15)
                .status(OperationalStatus.STOPPED)
                .events(Collections.singletonList(
                        Event.builder()
                                .eventId(new ObjectId())
                                .type(EventType.STOP_DEPARTURE)
                                .stopId(new ObjectId())
                                .timestamp(LocalDateTime.now().minusMinutes(5))
                                .details("Departed from stop")
                                .build()
                ))
                .build();

        Mockito.when(locationCommandRepository.save(any(LocationUpdate.class)))
                .thenReturn(Mono.just(sampleLocationUpdate));

        Mockito.when(locationCommandRepository.findById(eq(sampleLocationUpdate.getVehicleId())))
                .thenReturn(Mono.just(sampleLocationUpdate));

        Mockito.when(locationCommandRepository.deleteById(eq(sampleLocationUpdate.getVehicleId())))
                .thenReturn(Mono.empty());

        mockVehicle = Vehicle.builder()
                .vehicleId(fixedLocationId)
                .licensePlate("ABC-123")
                .capacity(50)
                .currentStatus(VehicleStatus.IN_SERVICE)
                .type(VehicleType.BUS)
                .driver(Driver.builder()
                        .driverId(new ObjectId())
                        .name("John Doe")
                        .contact(Contact.builder()
                                .email("johndoe@example.com")
                                .phone("+1234567890")
                                .build())
                        .build())
                .maintenanceDetails(MaintenanceDetails.builder()
                        .scheduledBy("Jane Smith")
                        .scheduledDate(LocalDate.now())
                        .details("Routine check-up")
                        .build())
                .currentLocation(new VehicleCoordinates(12.34, 56.78))
                .routeId(new ObjectId())
                .lastMaintenance(LocalDate.now().minusMonths(1))
                .build();

        Mockito.when(vehicleService.getVehicleById(any(String.class)))
                .thenReturn(Mono.just(mockVehicle));
    }

    @Test
    public void testCreateLocationUpdate() {
        assertNotNull(sampleLocationUpdate);

        webTestClient.post().uri("/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(sampleLocationUpdate), LocationUpdate.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LocationUpdate.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals(sampleLocationUpdate.getSpeed(), response.getSpeed());
                    assertEquals(sampleLocationUpdate.getDirection(), response.getDirection());
                    assertEquals(sampleLocationUpdate.getStatus(), response.getStatus());
                });
    }

    @Test
    public void testUpdateLocationUpdate() {
        ObjectId locationId = sampleLocationUpdate.getVehicleId();

        Mockito.when(locationCommandRepository.save(any(LocationUpdate.class)))
                .thenReturn(Mono.just(sampleLocationUpdate));

        webTestClient.put().uri("/locations/{id}", locationId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(sampleLocationUpdateDTO), LocationUpdateDTO.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LocationUpdate.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals(sampleLocationUpdateDTO.getSpeed(), response.getSpeed());
                    assertEquals(sampleLocationUpdateDTO.getStatus(), response.getStatus());
                });
    }

    @Test
    public void testDeleteLocationUpdate() {
        ObjectId locationId = sampleLocationUpdate.getVehicleId();

        webTestClient.delete().uri("/locations/{id}", locationId)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class);
    }
}
