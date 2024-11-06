package com.gft.location_query_microservice.application.service;

import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import reactor.core.publisher.Mono;

public interface LocationCommandService {

    Mono<LocationUpdate> saveLocationUpdate(LocationUpdate locationUpdate);

}
