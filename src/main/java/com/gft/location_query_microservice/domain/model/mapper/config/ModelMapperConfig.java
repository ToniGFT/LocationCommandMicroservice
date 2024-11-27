package com.gft.location_query_microservice.domain.model.mapper.config;

import com.gft.location_query_microservice.domain.events.LocationCreatedEvent;
import com.gft.location_query_microservice.domain.events.LocationUpdatedEvent;
import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;

public class ModelMapperConfig {

    public static ModelMapper getModelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);

        configureLocationMapping(modelMapper);

        return modelMapper;
    }

    private static void configureLocationMapping(ModelMapper modelMapper) {
        modelMapper.typeMap(LocationUpdate.class, LocationCreatedEvent.class)
                .addMappings(mapper -> {
                    mapper.map(LocationUpdate::getVehicleId, LocationCreatedEvent::setVehicleId);
                    mapper.map(LocationUpdate::getRouteId, LocationCreatedEvent::setRouteId);
                });

        modelMapper.typeMap(LocationUpdate.class, LocationUpdatedEvent.class)
                .addMappings(mapper -> {
                    mapper.map(LocationUpdate::getVehicleId, LocationUpdatedEvent::setVehicleId);
                    mapper.map(LocationUpdate::getRouteId, LocationUpdatedEvent::setRouteId);
                });
    }
}

