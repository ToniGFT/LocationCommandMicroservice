package com.gft.location_query_microservice.application.service;

import com.gft.location_query_microservice.application.dto.LocationUpdateDTO;
import com.gft.location_query_microservice.domain.exception.LocationNotFoundException;
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
    private LocationUpdateDTO locationUpdateDTO;

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

        locationUpdateDTO = LocationUpdateDTO.builder()
                .timestamp(null)
                .location(null)
                .speed(60.0)
                .direction(null)
                .routeId(new ObjectId("507f1f77bcf86cd799439013"))
                .passengerCount(10)
                .status(null)
                .events(null)
                .build();
    }

    @Test
    @DisplayName("Test saveLocationUpdate - Successful Save")
    void saveLocationUpdate_Success() {
        when(locationCommandRepository.save(locationUpdate)).thenReturn(Mono.just(locationUpdate));

        StepVerifier.create(locationService.saveLocationUpdate(locationUpdate))
                .expectNext(locationUpdate)
                .verifyComplete();

        verify(locationCommandRepository, times(1)).save(locationUpdate);
    }

    @Test
    @DisplayName("Test saveLocationUpdate - LocationSaveException")
    void saveLocationUpdate_LocationSaveException() {
        when(locationCommandRepository.save(locationUpdate)).thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(locationService.saveLocationUpdate(locationUpdate))
                .expectErrorMatches(throwable -> throwable instanceof LocationSaveException &&
                        throwable.getMessage().contains("Failed to save location update"))
                .verify();

        verify(locationCommandRepository, times(1)).save(locationUpdate);
    }

    @Test
    @DisplayName("Test updateLocationUpdate - Successful Update")
    void updateLocationUpdate_Success() {
        when(locationCommandRepository.findById(vehicleId)).thenReturn(Mono.just(locationUpdate));
        when(locationCommandRepository.save(locationUpdate)).thenReturn(Mono.just(locationUpdate));

        StepVerifier.create(locationService.updateLocationUpdate(vehicleId, locationUpdateDTO))
                .expectNextMatches(updatedLocation -> updatedLocation.getSpeed().equals(locationUpdateDTO.getSpeed()) &&
                        updatedLocation.getPassengerCount().equals(locationUpdateDTO.getPassengerCount()))
                .verifyComplete();

        verify(locationCommandRepository, times(1)).findById(vehicleId);
        verify(locationCommandRepository, times(1)).save(locationUpdate);
    }

    @Test
    @DisplayName("Test updateLocationUpdate - LocationNotFoundException")
    void updateLocationUpdate_LocationNotFoundException() {
        when(locationCommandRepository.findById(vehicleId)).thenReturn(Mono.empty());

        StepVerifier.create(locationService.updateLocationUpdate(vehicleId, locationUpdateDTO))
                .expectErrorMatches(throwable -> throwable instanceof LocationNotFoundException &&
                        throwable.getMessage().contains("Location update not found for vehicle ID: " + vehicleId))
                .verify();

        verify(locationCommandRepository, times(1)).findById(vehicleId);
        verify(locationCommandRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test deleteLocationUpdate - Successful Deletion")
    void deleteLocationUpdate_Success() {
        when(locationCommandRepository.findById(vehicleId)).thenReturn(Mono.just(locationUpdate));
        when(locationCommandRepository.deleteById(vehicleId)).thenReturn(Mono.empty());

        StepVerifier.create(locationService.deleteLocationUpdate(vehicleId))
                .verifyComplete();

        verify(locationCommandRepository, times(1)).findById(vehicleId);
        verify(locationCommandRepository, times(1)).deleteById(vehicleId);
    }

    @Test
    @DisplayName("Test deleteLocationUpdate - LocationNotFoundException")
    void deleteLocationUpdate_LocationNotFoundException() {
        when(locationCommandRepository.findById(vehicleId)).thenReturn(Mono.empty());

        StepVerifier.create(locationService.deleteLocationUpdate(vehicleId))
                .expectErrorMatches(throwable -> throwable instanceof LocationNotFoundException &&
                        throwable.getMessage().contains("Location update not found for vehicle ID: " + vehicleId))
                .verify();

        verify(locationCommandRepository, times(1)).findById(vehicleId);
        verify(locationCommandRepository, never()).deleteById(vehicleId);
    }
}
