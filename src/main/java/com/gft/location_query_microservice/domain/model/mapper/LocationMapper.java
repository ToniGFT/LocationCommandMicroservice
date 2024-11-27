package com.gft.location_query_microservice.domain.model.mapper;

import com.gft.location_query_microservice.application.dto.LocationUpdateDTO;
import com.gft.location_query_microservice.domain.events.LocationCreatedEvent;
import com.gft.location_query_microservice.domain.events.LocationUpdatedEvent;
import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import com.gft.location_query_microservice.domain.model.mapper.config.ModelMapperConfig;
import org.modelmapper.ModelMapper;

public class LocationMapper {

    private static final ModelMapper modelMapper = ModelMapperConfig.getModelMapper();

    private LocationMapper() {
    }

    public static void mapLocationData(LocationUpdateDTO source, LocationUpdate destination) {
        modelMapper.map(source, destination);
    }

    public static LocationCreatedEvent toLocationCreatedEvent(LocationUpdate locationUpdate) {
        return modelMapper.map(locationUpdate, LocationCreatedEvent.class);
    }

    public static LocationUpdatedEvent toLocationUpdatedEvent(LocationUpdate locationUpdate) {
        return modelMapper.map(locationUpdate, LocationUpdatedEvent.class);
    }
}

