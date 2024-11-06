package com.gft.location_query_microservice.application.controller;

import com.gft.location_query_microservice.application.response.service.LocationResponseService;
import com.gft.location_query_microservice.application.service.LocationCommandService;
import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("LocationCommandController Unit Tests")
class LocationCommandControllerTest {

    @Mock
    private LocationCommandService locationCommandService;

    @Mock
    private LocationResponseService locationResponseService;

    @InjectMocks
    private LocationCommandController locationCommandController;

    private LocationUpdate locationUpdate;
    private ObjectId objectId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectId = new ObjectId("507f1f77bcf86cd799439011");
        locationUpdate = LocationUpdate.builder()
                .vehicleId(objectId)
                .timestamp(null)
                .location(null)
                .speed(60.5)
                .direction(null)
                .routeId(new ObjectId("507f1f77bcf86cd799439012"))
                .passengerCount(10)
                .status(null)
                .events(null)
                .build();
    }

    @Test
    @DisplayName("Create LocationUpdate - Should Return Created Response")
    void createLocationUpdate_shouldReturnCreatedResponse() {
        // given
        when(locationCommandService.saveLocationUpdate(any(LocationUpdate.class))).thenReturn(Mono.just(locationUpdate));
        when(locationResponseService.buildCreatedResponse(locationUpdate))
                .thenReturn(Mono.just(ResponseEntity.status(201).body(locationUpdate)));

        // when
        Mono<ResponseEntity<LocationUpdate>> result = locationCommandController.createLocationUpdate(locationUpdate);

        // then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getBody() != null && response.getBody().getVehicleId().equals(objectId))
                .verifyComplete();
    }
}
