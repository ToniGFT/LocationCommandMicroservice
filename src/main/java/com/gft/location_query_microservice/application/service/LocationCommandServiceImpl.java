package com.gft.location_query_microservice.application.service;

import com.gft.location_query_microservice.domain.exception.LocationSaveException;
import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import com.gft.location_query_microservice.domain.repository.LocationCommandRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
}
