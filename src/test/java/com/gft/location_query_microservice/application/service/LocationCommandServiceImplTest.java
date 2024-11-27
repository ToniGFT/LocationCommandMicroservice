package com.gft.location_query_microservice.application.service;

import com.gft.location_query_microservice.application.dto.LocationUpdateDTO;
import com.gft.location_query_microservice.domain.exception.LocationNotFoundException;
import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import com.gft.location_query_microservice.domain.repository.LocationCommandRepository;
import com.gft.location_query_microservice.infraestructure.model.aggregates.Vehicle;
import com.gft.location_query_microservice.infraestructure.service.VehicleService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("LocationCommandServiceImpl Unit Tests")
class LocationCommandServiceImplTest {

    @InjectMocks
    private LocationCommandServiceImpl locationService;

    @Mock
    private LocationCommandRepository locationCommandRepository;

    @Mock
    private VehicleService vehicleService;

    private LocationUpdate locationUpdate;
    private LocationUpdateDTO locationUpdateDTO;
    private ObjectId vehicleId;
    private ObjectId routeId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        vehicleId = new ObjectId();
        routeId = new ObjectId();

        locationUpdate = LocationUpdate.builder()
                .vehicleId(vehicleId)
                .timestamp(null)
                .location(null)
                .speed(50.0)
                .direction(null)
                .routeId(routeId)
                .passengerCount(5)
                .status(null)
                .events(null)
                .build();

        locationUpdateDTO = LocationUpdateDTO.builder()
                .timestamp(null)
                .location(null)
                .speed(60.0)
                .direction(null)
                .routeId(routeId)
                .passengerCount(10)
                .status(null)
                .events(null)
                .build();
    }

    @Test
    @DisplayName("Test saveLocationUpdate - Successful Save with ID Validation")
    void testSaveLocationUpdate_Success() {
        when(vehicleService.getVehicleById(vehicleId.toHexString())).thenReturn(Mono.just(new Vehicle()));
        when(locationCommandRepository.save(any(LocationUpdate.class))).thenReturn(Mono.just(locationUpdate));

        StepVerifier.create(locationService.saveLocationUpdate(locationUpdate))
                .assertNext(savedLocation -> {
                    assert savedLocation.getVehicleId().equals(vehicleId);
                    assert savedLocation.getRouteId().equals(routeId);
                })
                .verifyComplete();

        verify(vehicleService, times(1)).getVehicleById(vehicleId.toHexString());
        verify(locationCommandRepository, times(1)).save(any(LocationUpdate.class));
    }

    @Test
    @DisplayName("Test saveLocationUpdate - Vehicle Not Found")
    void testSaveLocationUpdate_VehicleNotFound() {
        when(vehicleService.getVehicleById(vehicleId.toHexString())).thenReturn(Mono.empty());

        StepVerifier.create(locationService.saveLocationUpdate(locationUpdate))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException)
                .verify();

        verify(vehicleService, times(1)).getVehicleById(vehicleId.toHexString());
        verify(locationCommandRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test updateLocationUpdate - Successful Update with ID Validation")
    void updateLocationUpdate_Success() {
        when(locationCommandRepository.findById(vehicleId)).thenReturn(Mono.just(locationUpdate));
        when(locationCommandRepository.save(any(LocationUpdate.class))).thenAnswer(invocation -> {
            LocationUpdate updated = invocation.getArgument(0);
            return Mono.just(updated);
        });

        StepVerifier.create(locationService.updateLocationUpdate(vehicleId, locationUpdateDTO))
                .assertNext(updatedLocation -> {
                    assert updatedLocation.getVehicleId().equals(vehicleId);
                    assert updatedLocation.getRouteId().equals(locationUpdateDTO.getRouteId());
                    assert updatedLocation.getSpeed().equals(locationUpdateDTO.getSpeed());
                    assert updatedLocation.getPassengerCount().equals(locationUpdateDTO.getPassengerCount());
                })
                .verifyComplete();

        verify(locationCommandRepository, times(1)).findById(vehicleId);
        verify(locationCommandRepository, times(1)).save(any(LocationUpdate.class));
    }

    @Test
    @DisplayName("Test updateLocationUpdate - LocationNotFoundException")
    void updateLocationUpdate_LocationNotFoundException() {
        when(locationCommandRepository.findById(vehicleId)).thenReturn(Mono.empty());

        StepVerifier.create(locationService.updateLocationUpdate(vehicleId, locationUpdateDTO))
                .expectErrorMatches(throwable -> throwable instanceof LocationNotFoundException &&
                        throwable.getMessage().equals("Location update not found for vehicle ID: " + vehicleId))
                .verify();

        verify(locationCommandRepository, times(1)).findById(vehicleId);
        verify(locationCommandRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test deleteLocationUpdate - Successful Deletion with ID Validation")
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
                        throwable.getMessage().equals("Location update not found for vehicle ID: " + vehicleId))
                .verify();

        verify(locationCommandRepository, times(1)).findById(vehicleId);
        verify(locationCommandRepository, never()).deleteById(vehicleId);
    }
}
