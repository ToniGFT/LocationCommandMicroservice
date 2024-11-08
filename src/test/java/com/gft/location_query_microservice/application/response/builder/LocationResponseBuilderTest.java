package com.gft.location_query_microservice.application.response.builder;

import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LocationResponseBuilder Unit Tests")
class LocationResponseBuilderTest {

    @Test
    @DisplayName("Generate Created Response - Should Return 201 Created with LocationUpdate")
    void generateCreatedResponse_shouldReturnCreatedResponse() {
        // given
        ObjectId vehicleId = new ObjectId("507f1f77bcf86cd799439011");
        LocationUpdate locationUpdate = LocationUpdate.builder()
                .vehicleId(vehicleId)
                .timestamp(null)
                .location(null)
                .speed(50.0)
                .direction(null)
                .routeId(new ObjectId("507f1f77bcf86cd799439012"))
                .passengerCount(5)
                .status(null)
                .events(null)
                .build();

        // when
        ResponseEntity<LocationUpdate> response = LocationResponseBuilder.generateCreatedResponse(locationUpdate);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getVehicleId()).isEqualTo(vehicleId);
        assertThat(response.getBody().getSpeed()).isEqualTo(50.0);
    }

    @Test
    @DisplayName("Generate OK Response - Should Return 200 OK with LocationUpdate")
    void generateOkResponse_shouldReturnOkResponse() {
        // given
        ObjectId vehicleId = new ObjectId("507f1f77bcf86cd799439011");
        LocationUpdate locationUpdate = LocationUpdate.builder()
                .vehicleId(vehicleId)
                .timestamp(null)
                .location(null)
                .speed(60.0)
                .direction(null)
                .routeId(new ObjectId("507f1f77bcf86cd799439012"))
                .passengerCount(10)
                .status(null)
                .events(null)
                .build();

        // when
        ResponseEntity<LocationUpdate> response = LocationResponseBuilder.generateOkResponse(locationUpdate);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getVehicleId()).isEqualTo(vehicleId);
        assertThat(response.getBody().getSpeed()).isEqualTo(60.0);
    }

    @Test
    @DisplayName("Generate No Content Response - Should Return 204 No Content")
    void generateNoContentResponse_shouldReturnNoContentResponse() {
        // when
        ResponseEntity<Void> response = LocationResponseBuilder.generateNoContentResponse();

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }
}
