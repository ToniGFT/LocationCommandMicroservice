package com.gft.location_query_microservice.application.controller;

import com.gft.location_query_microservice.application.dto.LocationUpdateDTO;
import com.gft.location_query_microservice.application.response.service.LocationResponseService;
import com.gft.location_query_microservice.application.service.LocationCommandService;
import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/locations")
public class LocationCommandController {

    private static final Logger logger = LoggerFactory.getLogger(LocationCommandController.class);
    private final LocationCommandService locationCommandService;
    private final LocationResponseService locationResponseService;

    public LocationCommandController(LocationCommandService locationCommandService, LocationResponseService locationResponseService) {
        this.locationCommandService = locationCommandService;
        this.locationResponseService = locationResponseService;
    }

    @PostMapping
    public Mono<ResponseEntity<LocationUpdate>> createLocationUpdate(@Valid @RequestBody LocationUpdate locationUpdate) {
        logger.info("Attempting to save a new location update for vehicle ID: {}", locationUpdate.getVehicleId());
        return locationCommandService.saveLocationUpdate(locationUpdate)
                .flatMap(locationResponseService::buildCreatedResponse)
                .doOnSuccess(response -> logger.info("Successfully saved location update with ID: {}", response.getBody().getVehicleId()));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<LocationUpdate>> updateLocationUpdate(
            @PathVariable ObjectId id,
            @Valid @RequestBody LocationUpdateDTO locationUpdateDTO) {
        logger.info("Attempting to update location update with ID: {}", id);
        return locationCommandService.updateLocationUpdate(id, locationUpdateDTO)
                .flatMap(locationResponseService::buildOkResponse)
                .doOnSuccess(response -> logger.info("Successfully updated location update with ID: {}", id));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteLocationUpdate(@PathVariable ObjectId id) {
        logger.info("Attempting to delete location update with ID: {}", id);
        return locationCommandService.deleteLocationUpdate(id)
                .then(locationResponseService.buildNoContentResponse())
                .doOnSuccess(response -> logger.info("Successfully deleted location update with ID: {}", id));
    }
}
