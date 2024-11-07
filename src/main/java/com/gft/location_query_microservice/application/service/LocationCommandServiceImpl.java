package com.gft.location_query_microservice.application.service;

import com.gft.location_query_microservice.application.dto.LocationUpdateDTO;
import com.gft.location_query_microservice.domain.exception.LocationNotFoundException;
import com.gft.location_query_microservice.domain.exception.LocationSaveException;
import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import com.gft.location_query_microservice.domain.model.mapper.LocationMapper;
import com.gft.location_query_microservice.domain.repository.LocationCommandRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LocationCommandServiceImpl implements LocationCommandService {

    private final LocationCommandRepository locationUpdateRepository;

    @Override
    public Mono<LocationUpdate> saveLocationUpdate(@Valid LocationUpdate locationUpdate) {
        return locationUpdateRepository.save(locationUpdate)
                .onErrorMap(e -> new LocationSaveException("Failed to save location update", e));
    }

    @Override
    public Mono<LocationUpdate> updateLocationUpdate(ObjectId vehicleId, LocationUpdateDTO locationUpdateDTO) {
        return locationUpdateRepository.findById(vehicleId)
                .switchIfEmpty(Mono.error(new LocationNotFoundException("Location update not found for vehicle ID: " + vehicleId)))
                .flatMap(existingLocation -> {
                    LocationMapper.mapLocationData(locationUpdateDTO, existingLocation);
                    return locationUpdateRepository.save(existingLocation)
                            .onErrorMap(e -> new LocationSaveException("Failed to update location update", e));
                });
    }


    @Override
    public Mono<Void> deleteLocationUpdate(ObjectId vehicleId) {
        return locationUpdateRepository.findById(vehicleId)
                .switchIfEmpty(Mono.error(new LocationNotFoundException("Location update not found for vehicle ID: " + vehicleId)))
                .flatMap(existingLocation -> locationUpdateRepository.deleteById(vehicleId));
    }
}
