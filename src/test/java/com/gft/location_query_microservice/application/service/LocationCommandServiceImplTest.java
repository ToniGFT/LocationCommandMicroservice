package com.gft.location_query_microservice.application.service;

import com.gft.location_query_microservice.domain.exception.LocationSaveException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private ObjectId vehicleId;
    private ObjectId routeId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        vehicleId = ObjectId.get();
        routeId = ObjectId.get();
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
    }

    @Test
    @DisplayName("Test saveLocationUpdate - Successful Save")
    void testSaveLocationUpdate_Success() {
        when(vehicleService.getVehicleById(any(String.class))).thenReturn(Mono.just(new Vehicle()));
        when(locationCommandRepository.save(any(LocationUpdate.class))).thenReturn(Mono.just(locationUpdate));

        Mono<LocationUpdate> response = locationService.saveLocationUpdate(locationUpdate);

        assertEquals(locationUpdate, response.block());
        verify(vehicleService, times(1)).getVehicleById(any(String.class));
        verify(locationCommandRepository, times(1)).save(any(LocationUpdate.class));
    }

    @Test
    @DisplayName("Test saveLocationUpdate - Vehicle Not Found")
    void testSaveLocationUpdate_VehicleNotFound() {
        when(vehicleService.getVehicleById(any(String.class))).thenReturn(Mono.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            locationService.saveLocationUpdate(locationUpdate).block();
        });

        assertEquals("Vehicle not found with id: " + routeId, exception.getMessage());
        verify(vehicleService, times(1)).getVehicleById(any(String.class));
        verify(locationCommandRepository, never()).save(any(LocationUpdate.class));
    }

}
