package com.gft.location_query_microservice.application.response.service;

import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DisplayName("LocationResponseService Unit Tests")
class LocationResponseServiceTest {

    @InjectMocks
    private LocationResponseService locationResponseService;

    private LocationUpdate locationUpdate;
    private ObjectId vehicleId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        vehicleId = new ObjectId("507f1f77bcf86cd799439011");
        locationUpdate = LocationUpdate.builder()
                .vehicleId(vehicleId)
                .timestamp(null)
                .location(null)
                .speed(50.0)
                .direction(null)
                .routeId(new ObjectId("507f1f77bcf86cd799439012"))
                .passengerCount(5)
                .status(null)
                .events(null)
                .build();
    }

    @Test
    @DisplayName("Build Created Response - Should Return Mono with 201 Created")
    void buildCreatedResponse_shouldReturnCreatedResponse() {
        // given
        // when
        Mono<ResponseEntity<LocationUpdate>> result = locationResponseService.buildCreatedResponse(locationUpdate);

        // then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.CREATED &&
                        response.getBody() != null &&
                        response.getBody().getVehicleId().equals(vehicleId))
                .verifyComplete();
    }
}
