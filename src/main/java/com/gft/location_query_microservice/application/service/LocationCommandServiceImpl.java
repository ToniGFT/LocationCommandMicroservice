package com.gft.location_query_microservice.application.service;

import com.gft.location_query_microservice.application.dto.LocationUpdateDTO;
import com.gft.location_query_microservice.application.service.kafka.LocationEventPublisher;
import com.gft.location_query_microservice.domain.events.LocationDeletedEvent;
import com.gft.location_query_microservice.domain.exception.LocationNotFoundException;
import com.gft.location_query_microservice.domain.exception.LocationUpdateException;
import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import com.gft.location_query_microservice.domain.model.mapper.LocationMapper;
import com.gft.location_query_microservice.domain.repository.LocationCommandRepository;
import com.gft.location_query_microservice.infraestructure.service.VehicleService;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class LocationCommandServiceImpl implements LocationCommandService {

    private static final Logger logger = LoggerFactory.getLogger(LocationCommandServiceImpl.class);

    private final LocationCommandRepository locationRepository;
    private final LocationEventPublisher eventPublisher;
    private final VehicleService vehicleService;

    public LocationCommandServiceImpl(LocationCommandRepository locationRepository,
                                      LocationEventPublisher eventPublisher,
                                      VehicleService vehicleService) {
        this.locationRepository = locationRepository;
        this.eventPublisher = eventPublisher;
        this.vehicleService = vehicleService;
    }

    @Override
    public Mono<LocationUpdate> saveLocationUpdate(LocationUpdate locationUpdate) {
        return vehicleService.getVehicleById(locationUpdate.getVehicleId().toHexString())
                .flatMap(vehicle -> locationRepository.save(locationUpdate))
                .doOnSuccess(savedLocation -> logger.info("Location update saved successfully: {}", savedLocation))
                .flatMap(savedLocation -> eventPublisher
                        .publishLocationCreatedEvent(LocationMapper.toLocationCreatedEvent(savedLocation))
                        .doOnSuccess(v -> logger.info("LocationCreatedEvent published successfully"))
                        .thenReturn(savedLocation))
                .onErrorMap(e -> {
                    logger.error("Error occurred during location update creation: {}", e.getMessage());
                    return e;
                });
    }

    @Override
    public Mono<LocationUpdate> updateLocationUpdate(ObjectId id, LocationUpdateDTO locationUpdateDTO) {
        return locationRepository.findById(id)
                .switchIfEmpty(Mono.error(new LocationNotFoundException("Location not found with id: " + id)))
                .flatMap(existingLocation -> {
                    LocationMapper.mapLocationData(locationUpdateDTO, existingLocation);
                    return locationRepository.save(existingLocation);
                })
                .doOnSuccess(updatedLocation -> logger.info("Location update updated successfully: {}", updatedLocation))
                .flatMap(updatedLocation -> eventPublisher
                        .publishLocationUpdatedEvent(LocationMapper.toLocationUpdatedEvent(updatedLocation))
                        .thenReturn(updatedLocation))
                .onErrorMap(e -> new LocationUpdateException("Failed to update location: " + e.getMessage(), e));
    }

    @Override
    public Mono<Void> deleteLocationUpdate(ObjectId id) {
        return locationRepository.findById(id)
                .switchIfEmpty(Mono.error(new LocationNotFoundException("Location not found with id: " + id)))
                .flatMap(existingLocation -> locationRepository.deleteById(id)
                        .then(eventPublisher.publishLocationDeletedEvent(
                                LocationDeletedEvent.builder()
                                        .locationId(existingLocation.getVehicleId())
                                        .build()
                        )));
    }
}
