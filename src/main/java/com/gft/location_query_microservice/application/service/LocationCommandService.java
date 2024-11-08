package com.gft.location_query_microservice.application.service;

import com.gft.location_query_microservice.application.dto.LocationUpdateDTO;
import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import org.bson.types.ObjectId;
import reactor.core.publisher.Mono;

public interface LocationCommandService {

    Mono<LocationUpdate> saveLocationUpdate(LocationUpdate locationUpdate);

    Mono<LocationUpdate> updateLocationUpdate(ObjectId id, LocationUpdateDTO locationUpdateDTO);

    Mono<Void> deleteLocationUpdate(ObjectId vehicleId);
}
