package com.gft.location_query_microservice.application.service;

import com.gft.location_query_microservice.domain.exception.LocationSaveException;
import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import com.gft.location_query_microservice.domain.repository.LocationCommandRepository;
import com.gft.location_query_microservice.infraestructure.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LocationCommandServiceImpl implements LocationCommandService {

    private final LocationCommandRepository locationUpdateRepository;
    private final VehicleService vehicleService;

    @Override
    public Mono<LocationUpdate> saveLocationUpdate(@Valid LocationUpdate locationUpdate) {
        return vehicleService.getVehicleById(locationUpdate.getVehicleId().toHexString())
                .flatMap(route -> {
                    return locationUpdateRepository.save(locationUpdate);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Vehicle not found with id: " + locationUpdate.getRouteId().toString())));
    }
}
