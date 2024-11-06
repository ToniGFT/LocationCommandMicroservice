package com.gft.location_query_microservice.application.service;

import com.gft.location_query_microservice.domain.exception.LocationSaveException;
import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import com.gft.location_query_microservice.domain.repository.LocationCommandRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@DisplayName("LocationCommandServiceImpl Unit Tests")
class LocationCommandServiceImplTest {

    @Mock
    private LocationCommandRepository locationCommandRepository;

    @InjectMocks
    private LocationCommandServiceImpl locationService;

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
    @DisplayName("Test saveLocationUpdate - Successful Save")
    void saveLocationUpdate_Success() {
        // Arrange
        when(locationCommandRepository.save(locationUpdate)).thenReturn(Mono.just(locationUpdate));

        // Act & Assert
        StepVerifier.create(locationService.saveLocationUpdate(locationUpdate))
                .expectNext(locationUpdate)
                .verifyComplete();

        verify(locationCommandRepository, times(1)).save(locationUpdate);
    }

    @Test
    @DisplayName("Test saveLocationUpdate - LocationSaveException")
    void saveLocationUpdate_LocationSaveException() {
        // Arrange
        when(locationCommandRepository.save(locationUpdate)).thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(locationService.saveLocationUpdate(locationUpdate))
                .expectErrorMatches(throwable -> throwable instanceof LocationSaveException &&
                        throwable.getMessage().contains("Failed to save location update"))
                .verify();

        verify(locationCommandRepository, times(1)).save(locationUpdate);
    }
}
