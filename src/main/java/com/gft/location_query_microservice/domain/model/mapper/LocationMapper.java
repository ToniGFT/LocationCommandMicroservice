package com.gft.location_query_microservice.domain.model.mapper;

import com.gft.location_query_microservice.application.dto.LocationUpdateDTO;
import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import org.modelmapper.ModelMapper;

public class LocationMapper {

    private static final ModelMapper modelMapper = new ModelMapper();

    private LocationMapper() {
    }

    public static void mapLocationData(LocationUpdateDTO source, LocationUpdate destination) {
        modelMapper.map(source, destination);
    }
}
