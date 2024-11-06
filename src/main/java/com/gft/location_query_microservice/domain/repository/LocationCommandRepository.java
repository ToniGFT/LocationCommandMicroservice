package com.gft.location_query_microservice.domain.repository;

import com.gft.location_query_microservice.domain.model.aggregates.LocationUpdate;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationCommandRepository extends ReactiveMongoRepository<LocationUpdate, ObjectId> {
}
