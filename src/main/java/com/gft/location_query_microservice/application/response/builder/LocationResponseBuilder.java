package com.gft.location_query_microservice.application.response.builder;

import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class LocationResponseBuilder {

    public static ResponseEntity<LocationUpdate> generateCreatedResponse(LocationUpdate locationUpdate) {
        return ResponseEntity.status(HttpStatus.CREATED).body(locationUpdate);
    }

    public static ResponseEntity<LocationUpdate> generateOkResponse(LocationUpdate locationUpdate) {
        return ResponseEntity.ok(locationUpdate);
    }

    public static ResponseEntity<Void> generateNoContentResponse() {
        return ResponseEntity.noContent().build();
    }
}
