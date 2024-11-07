package com.gft.location_query_microservice.domain.model.mapper;

import com.gft.location_query_microservice.application.dto.LocationUpdateDTO;
import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import com.gft.location_query_microservice.domain.model.valueobject.Coordinates;
import com.gft.location_query_microservice.domain.model.valueobject.Event;
import com.gft.location_query_microservice.domain.model.valueobject.enums.Direction;
import com.gft.location_query_microservice.domain.model.valueobject.enums.EventType;
import com.gft.location_query_microservice.domain.model.valueobject.enums.OperationalStatus;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LocationMapper Unit Tests")
class LocationMapperTest {

    private LocationUpdateDTO locationUpdateDTO;
    private LocationUpdate locationUpdate;

    @BeforeEach
    void setUp() {
        locationUpdateDTO = LocationUpdateDTO.builder()
                .timestamp(LocalDateTime.now())
                .location(Coordinates.builder().latitude(40.712776).longitude(-74.005974).build())
                .speed(45.0)
                .direction(Direction.NORTH)
                .routeId(new ObjectId())
                .passengerCount(30)
                .status(OperationalStatus.ON_ROUTE)
                .events(List.of(
                        Event.builder()
                                .eventId(new ObjectId())
                                .type(EventType.STOP_ARRIVAL)
                                .stopId(new ObjectId())
                                .timestamp(LocalDateTime.now())
                                .details("Bus stopped at Central Station")
                                .build()
                ))
                .build();

        locationUpdate = LocationUpdate.builder().build();
    }


    @Test
    @DisplayName("Map LocationUpdateDTO to LocationUpdate - Should Map All Fields Correctly")
    void mapLocationData_shouldMapAllFieldsCorrectly() {
        // Act
        LocationMapper.mapLocationData(locationUpdateDTO, locationUpdate);

        // Assert
        assertThat(locationUpdate.getTimestamp()).isEqualTo(locationUpdateDTO.getTimestamp());
        assertThat(locationUpdate.getLocation().getLatitude()).isEqualTo(locationUpdateDTO.getLocation().getLatitude());
        assertThat(locationUpdate.getLocation().getLongitude()).isEqualTo(locationUpdateDTO.getLocation().getLongitude());
        assertThat(locationUpdate.getSpeed()).isEqualTo(locationUpdateDTO.getSpeed());
        assertThat(locationUpdate.getDirection()).isEqualTo(locationUpdateDTO.getDirection());
        assertThat(locationUpdate.getRouteId()).isEqualTo(locationUpdateDTO.getRouteId());
        assertThat(locationUpdate.getPassengerCount()).isEqualTo(locationUpdateDTO.getPassengerCount());
        assertThat(locationUpdate.getStatus()).isEqualTo(locationUpdateDTO.getStatus());
        assertThat(locationUpdate.getEvents().size()).isEqualTo(locationUpdateDTO.getEvents().size());
        assertThat(locationUpdate.getEvents().get(0).getEventId()).isEqualTo(locationUpdateDTO.getEvents().get(0).getEventId());
        assertThat(locationUpdate.getEvents().get(0).getType()).isEqualTo(locationUpdateDTO.getEvents().get(0).getType());
        assertThat(locationUpdate.getEvents().get(0).getStopId()).isEqualTo(locationUpdateDTO.getEvents().get(0).getStopId());
        assertThat(locationUpdate.getEvents().get(0).getTimestamp()).isEqualTo(locationUpdateDTO.getEvents().get(0).getTimestamp());
    }
}
