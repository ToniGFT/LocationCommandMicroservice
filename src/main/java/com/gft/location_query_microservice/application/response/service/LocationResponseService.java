package com.gft.location_query_microservice.application.response.service;

import com.gft.location_query_microservice.application.response.builder.LocationResponseBuilder;
import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class LocationResponseService {

    public Mono<ResponseEntity<LocationUpdate>> buildCreatedResponse(LocationUpdate locationUpdate) {
        return Mono.fromCallable(() -> LocationResponseBuilder.generateCreatedResponse(locationUpdate));
    }

    public Mono<ResponseEntity<LocationUpdate>> buildOkResponse(LocationUpdate locationUpdate) {
        return Mono.fromCallable(() -> LocationResponseBuilder.generateOkResponse(locationUpdate));
    }

    public Mono<ResponseEntity<Void>> buildNoContentResponse() {
        return Mono.just(LocationResponseBuilder.generateNoContentResponse());
    }
}
